(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  {:init (fn []
           ;; this is a vector as specified in gen-class/init
           [[] (atom {:stack ()})])
   
   :push (fn [x]
           (.swap this :stack #(conj % x)))

   :pop (fn []
          (let [x (first (.get this :stack))]
            (.swap this :stack rest)
            x))

   :peek (fn []
           (first (.get this :stack)))})
