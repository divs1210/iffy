(ns iffy.core-test
  (:require [clojure.java.io :as f]
            [clojure.test :refer :all])
  (:import [iffy.examples DurableStack Stack StrangeLoop]))

(deftest Stack-tests
  (let [s (Stack.)]

    (is (empty? (:stack s))
        "fresh stack is empty")

    (testing "push"
      (.push s 1)
      (is (= [1] (:stack s))
          ".push puts x on stack")

      (.push s 2)
      (is (= [2 1] (:stack s))
          ".push puts new item on top"))

    (testing "pop"
      (is (= 2 (.pop s))
          ".pop returns item on top")

      (is (= [1] (:stack s))
          ".pop removes item on top"))))


(deftest DurableStack-tests
  (let [f "DurableStack-test"
        s (doto (DurableStack.)
            (.setFile f))
        z (doto (DurableStack.)
            (.setFile f))]

    (testing "inheritence"
      (doseq [x (take 10 (shuffle (range 100)))]
        (.push s x))

      (is (= 10 (count (:stack s)))
          "superclass works as expected"))

    (testing "persistence"
      (.save s)
      (.load z)
      (is (= (:stack s)
             (:stack z))
          ".save and .load work as expected")

      (f/delete-file f))))


(deftest StrangeLoop-tests
  (let [f (StrangeLoop.)]

    (is (= (reverse "Hello")
           (f "Hello"))
        "StrangeLoop extends AFn, and is thus a Clojure function")))
