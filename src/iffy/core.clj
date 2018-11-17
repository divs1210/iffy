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
  '{:get (meth [key]
           (get @state key))

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
  (let [extends-and-implements (concat extends-and-implements ['iffy.core.IObj])
        super-ctor-args (->> methods :super vec)]
    `(do
       (gen-class
        :name
        ~(symbol (munge (str *ns* "." cname)))

        :extends
        ~(let [class-or-interface (eval (first extends-and-implements))]
           (if (.isInterface class-or-interface)
             Object
             class-or-interface))

        :implements
        ~(vec (let [class-or-interface (first (map eval extends-and-implements))]
                (if (.isInterface class-or-interface)
                  extends-and-implements
                  (rest extends-and-implements))))

        :init
        ~'super-call

        :state
        ~'state

        :prefix
        ~(str cname "-")

        :methods
        ~(vec (for [[m-name [m-type m-args & _]] (dissoc methods :super)
                    :when (not= 'over m-type)]
                [(symbol (name m-name))
                 (vec (repeat (count m-args) 'Object))
                 'Object])))

       (defn ~(symbol (str cname "-super-call")) []
         [~super-ctor-args (atom {})])

       ~@(for [[m-name [_ m-args & m-body]] (-> methods
                                                (dissoc :super)
                                                (merge IObj-methods))]
           `(defn ~(symbol (str cname "-" (name m-name)))
              [~'this ~@m-args]
              (let [~'state (.state ~'this)]
                ~@m-body))))))
