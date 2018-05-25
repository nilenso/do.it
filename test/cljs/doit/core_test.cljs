(ns doit.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [doit.events :as events]))

(deftest test-get-todo-success
  (testing "if retrieved todos are added to app db"
    (let [old-db {:todos {1 {:content "old content" :id 1}} :other-keys :some-vals}
          todo1 {:content "updated todo 1" :id 1}
          todo2 {:content "new todo 2" :id 2}
          todos [todo1 todo2]
          new-db (events/get-todo-success old-db [:get-todo-success todos])]
      (is (= (:todos new-db) {1 todo1
                              2 todo2})))))
