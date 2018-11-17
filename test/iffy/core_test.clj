(ns iffy.core-test
  (:require [clojure.java.io :as f]
            [clojure.test :refer :all])
  (:import [iffy.examples DurableStack Stack]))

(deftest Stack-tests
  (let [s (Stack.)]
    (is (empty? (.get s :stack))
        "fresh stack is empty")

    (testing "push"
      (.push s 1)
      (is (= [1] (.get s :stack))
          ".push puts x on stack")

      (.push s 2)
      (is (= [2 1] (.get s :stack))
          ".push puts new item on top"))

    (testing "pop"
      (is (= 2 (.pop s))
          ".pop returns item on top")

      (is (= [1] (.get s :stack))
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

      (is (= 10 (count (.get s :stack)))
          "superclass works as expected"))

    (testing "persistence"
      (.save s)
      (.load z)
      (is (= (.get s :stack)
             (.get z :stack))
          ".save and .load work as expected")

      (f/delete-file f))))
