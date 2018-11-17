(ns iffy.examples
  (:require [iffy.core :refer :all]))

(defclass Stack []
  (meth push [x]
    (.update this :stack #(cons x %)))

  (meth pop []
    (let [x (first (:stack this))]
      (.update this :stack rest)
      x))

  (meth peek []
    (first (:stack this))))


(defclass DurableStack [iffy.examples.Stack]
  (meth setFile [file]
    (.reset this :file file))

  (meth save []
    (spit (:file this)
          (pr-str @state)))

  (meth load []
    (reset! state
            (read-string (slurp (:file this))))))


(defclass StrangeLoop [clojure.lang.AFn]
  (over invoke [x]
    (reverse x)))


(defclass SmartList [java.util.ArrayList]
  (ctors [[] [int]])

  (over get [idx]
    (.superGet this (mod idx (.size this))))

  (meth map [f]
    (dotimes [i (.size this)]
      (let [curr-val (.get this i)
            new-val (f curr-val)]
        (.set this i new-val)))))
