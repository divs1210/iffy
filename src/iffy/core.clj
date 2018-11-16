(ns iffy.core)

;; macro to create class
;; =====================
(defmacro defclass
  "Creates a Java class
  cname - class name
  extends-and-implements - vector of the form [Class Interface1 Interface2]
  methods - a map of the form {:name (fn [x] (oswap this :val + x))}"
  [cname extends-and-implements methods]
  `(do
     (gen-class
      :name
      ~(symbol (munge (str *ns* "." cname)))

      :extends
      ~(let [class-or-interface (first extends-and-implements)]
         (if (class? class-or-interface)
           class-or-interface
           'java.lang.Object))
      
      :implements
      ~(vec (let [class-or-interface (first extends-and-implements)]
              (if (class? class-or-interface)
                (rest extends-and-implements)
                extends-and-implements)))

      :init
      ~'init

      :state
      ~'state

      :prefix
      ~(str cname "-")
      
      :methods
      ~(vec (for [mname (-> methods (dissoc :init) keys)]
              [(symbol (name mname)) ['java.util.Map] 'Object])))

     (def ~(symbol (str cname "-init"))
       ~(:init methods))

     ~@(for [[m-name [_ m-args & m-body]] (dissoc methods :init)]
         `(defn ~(symbol (str cname "-" (name m-name)))
            [~'this {:keys ~m-args}]
            (let [~'state (.state ~'this)]
              ~@m-body)))))


;; utils
;; =====
(defn oget [obj key]
  (-> obj .state deref key))

(defn oswap [this key f & args]
  (swap! (.state this) update key #(apply f % args)))
