(ns iffy.core)

;; bootstrap
;; =========
(defn ctors
  "Used to expose Java super constructors for derived class."
  [arg-signatures]
  (throw (Exception. "ctors used outside defclass!")))

(defn meth
  "Like `defn`, but used to define Java methods.
  Automatically introduces special bindings:
  `this`  - the current object
  `state` - alias for `this.state`, an atom with a hashmap, for storing mutable state"
  [name argv & body]
  (throw (Exception. "meth used outside defclass!")))

(defn over
  "Like `defn`, but used to override Java methods.
  Automatically introduces special bindings:
  `this`  - the current object
  `state` - alias for `this.state`, an atom with a hashmap, for storing mutable state
  `super<Name>` - where `<Name>` is `name` with the first letter capitalized"
  [name argv & body]
  (throw (Exception. "over used outside defclass!")))

(gen-interface
 :name iffy.core.IObj
 :methods [[update [clojure.lang.Keyword clojure.lang.IFn] Object]
           [reset [clojure.lang.Keyword Object] Object]])

(def default-methods
  '{:valAt (over [key]
             (get @state key nil))

    :update (over [key f]
              (swap! state update key f))

    :reset (over [key val]
             (swap! state assoc key val))})


;; cream
;; =====
(defmacro defclass
  "Creates a Java class
  cname - class name
  extends-and-implements - vector of the form
    * [Class Interface1 Interface2 ...], or
    * [Interface1 Interface2 ...]
  methods - should have one of following forms:
    * (ctors [[] [String] [int]])
    * (meth methName [x y] ...)
    * (over methName [] (.superMethName this) ...)"
  [cname extends-and-implements & methods]
  (let [extends-and-implements (map eval (concat extends-and-implements
                                                 ['iffy.core.IObj
                                                  'clojure.lang.ILookup]))
        methods (->> (map (fn [[m-type & m-body]]
                            (if (= 'ctors m-type)
                              [:supers (first m-body)]
                              (let [[name argv & body] m-body]
                                [(keyword name) (concat (list m-type argv)
                                                        body)])))
                          methods)
                     vec
                     (into {}))
        supers (or (:supers methods) [[]])
        methods (dissoc methods :supers)]
    `(do
       (gen-class
        :name
        ~(symbol (munge (str *ns* "." cname)))

        :extends
        ~(let [class-or-interface (first extends-and-implements)]
           (if (.isInterface class-or-interface)
             Object
             class-or-interface))

        :implements
        ~(vec (let [class-or-interface (first extends-and-implements)]
                (if (.isInterface class-or-interface)
                  extends-and-implements
                  (rest extends-and-implements))))

        :init
        ~'super-call

        :constructors
        ~(->> (for [types supers
                    :let [types (vec types)]]
                [types types])
              vec
              (into {}))

        :state
        ~'state

        :prefix
        ~(str cname "-")

        :exposes-methods
        ~(->> (for [[m-name [m-type m-args & _]] methods
                    :when (= 'over m-type)
                    :let [m-name (name m-name)]]
                [(symbol m-name)
                 (symbol (str 'super
                              (Character/toUpperCase (first m-name))
                              (apply str (rest m-name))))])
              vec
              (into {}))

        :methods
        ~(vec (for [[m-name [m-type m-args & _]] methods
                    :when (not= 'over m-type)]
                [(symbol (name m-name))
                 (vec (repeat (count m-args) 'Object))
                 'Object])))

       (defn ~(symbol (str cname "-super-call"))
         ~@(for [types supers
                 :let [names (vec (repeatedly (count types) gensym))]]
             (list names
                   [names '(atom {})])))

       ~@(for [[m-name [_ m-args & m-body]] (merge default-methods methods)]
           `(defn ~(symbol (str cname "-" (name m-name)))
              [~'this ~@m-args]
              (let [~'state (.state ~'this)]
                ~@m-body))))))
