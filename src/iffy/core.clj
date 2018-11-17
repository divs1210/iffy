(ns iffy.core)

;; bootstrap
;; =========
(gen-interface
 :name iffy.core.IObj
 :methods [[get [clojure.lang.Keyword] Object]
           [set [clojure.lang.Keyword Object] void]
           [swap [clojure.lang.Keyword clojure.lang.IFn] Object]])

(defn meth
  "Like `fn`, but used to define Java methods"
  [argv & body]
  (throw (Exception. "meth used outside defclass!")))

(defn over
  "Like `fn`, but used to override Java methods"
  [argv & body]
  (throw (Exception. "meth used outside defclass!")))

(def IObj-methods
  '{:valAt (over [key]
             (get @state key nil))

    :set (meth [key val]
           (swap! state assoc key val))

    :swap (meth [key f]
            (swap! state update key f))})


;; cream
;; =====
(defmacro defclass
  "Creates a Java class
  cname - class name
  extends-and-implements - vector of the form [Class Interface1 Interface2]
  methods - a map of the form {:name (fn [x] (oswap this :val + x))}"
  [cname extends-and-implements methods]
  (let [extends-and-implements (map eval (concat extends-and-implements ['iffy.core.IObj
                                                                         'clojure.lang.ILookup]))]
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

       (defn ~(symbol (str cname "-super-call")) []
         [[] (atom {})])

       ~@(for [[m-name [_ m-args & m-body]] (merge IObj-methods methods)]
           `(defn ~(symbol (str cname "-" (name m-name)))
              [~'this ~@m-args]
              (let [~'state (.state ~'this)]
                ~@m-body))))))
