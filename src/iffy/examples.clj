(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  {:init (fn []
           [[] (atom {:stack ()})])
   
   :push (fn [x]
           (swap! (.state this) update :stack conj x))

   :pop (fn []
          (let [state (.state this)
                x (-> @state :stack first)]
           (swap! state update :stack rest)
           x))

   :peek (fn []
           (first @(.state this)))})
