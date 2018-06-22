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

(deftest list-todo-lists-query-test
  (testing "User can list the todo lists"
    (let [new-list {:name "new list"}
          _        (todo-list-db/create! new-list)
          response (todo-list-db/list-all)]
      (is (= (count response) 2)) ;; The default list is also present
      (is (= (:name (last response)) (:name new-list)))
      (is (= (set (keys (last response))) #{:name :id})))))

(deftest edit-todo-list-test
  (testing "User can edit a todo-list"
    (let [params         {:name "test list"}
          todo-list      (todo-list-db/create! params)
          updated-params {:name "updated test list"}
          response       (todo-list-db/update! updated-params)]
      (is (= response (select-keys response (keys updated-params)))))))
