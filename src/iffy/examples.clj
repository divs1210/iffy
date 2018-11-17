(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  {:push (meth [x]
           (.swap this :stack #(conj % x)))

   :pop (meth []
          (let [x (first (.get this :stack))]
            (.swap this :stack rest)
            x))

   :peek (meth []
           (first (.get this :stack)))})


(defclass DurableStack [iffy.examples.Stack]
  {:setFile (meth [f]
              (.set this :f f))

   :save (meth []
           (spit (@state :f) (pr-str (dissoc @state :f))))

   :load (meth []
           (let [f (@state :f)]
             (reset! state
                     (assoc (read-string (slurp f))
                            :f f))))})


(defclass StrangeLoop [clojure.lang.AFn]
  {:setFn (meth [f]
            (.set this :f f))
   
   :invoke (over [x]
             ((.get this :f) x))})
