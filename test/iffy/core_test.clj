(ns iffy.core-test
  (:require [clojure.java.io :as f]
            [clojure.test :refer :all])
  (:import [iffy.examples DurableStack SmartList Stack StrangeLoop]))

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
        s1 (doto (DurableStack.)
            (.setFile f))
        s2 (doto (DurableStack.)
            (.setFile f))]

    (testing "inheritence"
      (doseq [x (take 10 (shuffle (range 100)))]
        (.push s1 x))

      (is (= 10 (count (:stack s1)))
          "superclass works as expected"))

    (testing "persistence"
      (.save s1)
      (.load s2)
      (is (= (:stack s1)
             (:stack s2))
          ".save and .load work as expected")

      (f/delete-file f))))


(deftest StrangeLoop-tests
  (let [f (StrangeLoop.)]

    (is (= (reverse "Hello")
           (f "Hello"))
        "StrangeLoop extends AFn, and is thus a Clojure function")))


(deftest SmartList-tests
  (let [l1 (doto (SmartList.)
             (.addAll (range 10)))
        l2 (doto (SmartList. 5)
             (.addAll (range 10)))]

    (is (= l1 l2)
        "both constructors work")

    (is (= 3 (.get l1 3))
        "works correctly for normal indexes")

    (is (= 8 (.get l1 -2))
        "works correctly for negative indexes")

    (.map l1 #(* % 2))
    (is (= (map #(* % 2) (range 10))
           l1)
        "additional method works")))
