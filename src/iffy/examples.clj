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
           (spit (:f this)
                 (pr-str @state)))

   :load (meth []
           (reset! state
                   (read-string (slurp (:f this)))))})


(defclass StrangeLoop [clojure.lang.AFn]
  {:invoke (over [x]
             (reverse x))})
