(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  {:init (fn []
           ;; this is a vector as specified in gen-class/init
           [[] (atom {:stack ()})])
   
   :push (fn [x]
           (oswap this :stack conj x))

   :pop (fn []
          (let [x (first (oget this :stack))]
            (oswap this :stack rest)
            x))

   :peek (fn []
           (first (oget this :stack)))})
