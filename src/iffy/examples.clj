(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  {:init (fn []
           ;; this is a vector as specified in gen-class/init
           [[] (atom {})])
   
   :push (fn [x]
           (.swap this :stack #(conj % x)))

   :pop (fn []
          (let [x (first (.get this :stack))]
            (.swap this :stack rest)
            x))

   :peek (fn []
           (first (.get this :stack)))})


(defclass DurableStack [iffy.examples.Stack]
  {:init (fn []
           [[] (atom {})])

   :setFile (fn [f]
              (.set this :f f))

   :save (fn []
           (spit (@state :f) (pr-str (dissoc @state :f))))

   :load (fn []
           (let [f (@state :f)]
             (reset! state
                     (assoc (read-string (slurp f))
                            :f f))))})
