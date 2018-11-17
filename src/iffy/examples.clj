(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  (meth push [x]
    (.swap this :stack #(conj % x)))

  (meth pop []
    (let [x (first (:stack this))]
      (.swap this :stack rest)
      x))

  (meth peek []
    (first (:stack this))))


(defclass DurableStack [iffy.examples.Stack]
  (meth setFile [f]
    (.swap state assoc :f f))

  (meth save []
    (spit (:f this)
          (pr-str @state)))

  (meth load []
    (reset! state
            (read-string (slurp (:f this))))))


(defclass StrangeLoop [clojure.lang.AFn]
  (over invoke [x]
    (reverse x)))


(defclass SmartList [java.util.ArrayList]
  (ctors [[] [int]])

  (over get [idx]
    (.superGet this (mod idx (.size this))))

  (meth map [f]
    (dotimes [i (.size this)]
      (.set this i (f (.get this i))))))
