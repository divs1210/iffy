(ns iffy.core)

;; bootstrap
;; =========
(gen-interface
 :name iffy.core.IObj
 :methods [[get [clojure.lang.Keyword] Object]
           [set [clojure.lang.Keyword Object] void]
           [swap [clojure.lang.Keyword clojure.lang.IFn] Object]])

(def IObj-methods
  '{:get (fn [key]
           (get @state key))

    :set (fn [key val]
           (swap! state assoc key val))

    :swap (fn [key f]
            (swap! state update key f))})


;; cream
;; =====
(defmacro defclass
  "Creates a Java class
  cname - class name
  extends-and-implements - vector of the form [Class Interface1 Interface2]
  methods - a map of the form {:name (fn [x] (oswap this :val + x))}"
  [cname extends-and-implements methods]
  (let [extends-and-implements (concat extends-and-implements ['iffy.core.IObj])]
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
        ~'init

        :state
        ~'state

        :prefix
        ~(str cname "-")

        :methods
        ~(vec (for [[m-name [_ m-args & _]] (dissoc methods :init)]
                [(symbol (name m-name))
                 (vec (repeat (count m-args) 'Object))
                 'Object])))

       (def ~(symbol (str cname "-init"))
         ~(:init methods))

       ~@(for [[m-name [_ m-args & m-body]] (-> methods
                                                (dissoc :init)
                                                (merge IObj-methods))]
           `(defn ~(symbol (str cname "-" (name m-name)))
              [~'this ~@m-args]
              (let [~'state (.state ~'this)]
                ~@m-body))))))
