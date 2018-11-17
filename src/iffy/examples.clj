(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  {:push (meth [x]
           (.swap this :stack #(conj % x)))

   :pop (meth []
          (let [x (first (:stack this))]
            (.swap this :stack rest)
            x))

   :peek (meth []
           (first (:stack this)))})


(defclass DurableStack [iffy.examples.Stack]
  {:setFile (meth [f]
              (.set this :f f))

   :save (meth []
           (spit (:f this) (pr-str (dissoc @state :f))))

   :load (meth []
           (let [f (:f this)]
             (reset! state
                     (assoc (read-string (slurp f))
                            :f f))))})


(defclass StrangeLoop [clojure.lang.AFn]
  {:setFn (meth [f]
            (.set this :f f))
   
   :invoke (over [x]
             ((:f this) x))})
