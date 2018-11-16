(ns iffy.core-test
  (:require [clojure.test :refer :all])
  (:import [iffy.examples Stack]))

(deftest tests
  (let [s (Stack.)]
    (is (empty? (-> s .state deref :state))
        "fresh stack is empty")

    (testing "push"
      (.push s {:x 1})
      (is (= [1] (-> s .state deref :stack))
          ".push puts x on stack")

      (.push s {:x 2})
      (is (= [2 1] (-> s .state deref :stack))
          ".push puts new item on top"))

    (testing "pop"
      (is (= 2 (.pop s {}))
          ".pop returns item on top")

      (is (= [1] (-> s .state deref :stack))
          ".pop removes item on top"))))
