(ns doit.todo-list.db-test
  (:require [doit.todo-list.db :as todo-list-db]
            [clojure.test :refer :all]
            [doit.fixtures :as fixtures]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db)
(use-fixtures :each fixtures/isolate-db)

(deftest default-list
  (testing "the default list is present"
    (let [response (todo-list-db/list-all)]
      (is (= 1 (count response))))))

(deftest create-todo-list-test
  (testing "User can create a todo list"
    (let [name     "New List"
          response (todo-list-db/create! {:name name})]
      (is (= (:name response) name))
      (is (= (set (keys response)) #{:name :id})))))
