(ns doit.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [doit.events :as events]))

(deftest test-get-todo-success
  (testing "if retrieved todos are added to app db"
    (let [old-db {:todos [{:content "old content" :id 1}] :other-keys :some-vals}
          todos [{:content "new todo 1" :id 2}
                 {:content "new todo 2" :id 3}]
          new-db (events/get-todo-success old-db [:get-todo-success todos])]
      (is (= (:todos new-db) todos)))))
