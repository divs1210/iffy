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
  (let [s (DurableStack.)
        f "DurableStack-test"]
    (.setFile s f)

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
          ".pop removes item on top"))

    (testing "persistence"
      (doseq [x (random-sample 0.2 (range 100))]
        (.push s x))

      (.save s)

      (let [s2 (DurableStack.)]
        (.setFile s2 f)
        (.load s2)

        (is (= (.get s :stack)
               (.get s2 :stack))))

      (f/delete-file f))))
