(ns doit.todo.db-test
  (:require [doit.todo.db :as todo-db]
            [clojure.test :refer :all]
            [doit.fixtures :as fixtures]))

(use-fixtures :once fixtures/load-config fixtures/create-migrate-destroy-db)
(use-fixtures :each fixtures/isolate-db)

(deftest add-todo-query-test
  (testing "User can add a todo to db"
    (let [params {:content "test todo"}
          response (todo-db/add-todo! params)]
      (is (= (:content response) (:content params)))
      (is (= (:done response) false))
      (is (= (set (keys response)) #{:content :id :created_at :done})))))

(deftest list-todos-query-test
  (testing "User can list the todos"
    (let [test-todo1 {:content "test todo 1"}
          test-todo2 {:content "test todo 2"}
          _ (todo-db/add-todo! test-todo1)
          _ (todo-db/add-todo! test-todo2)
          res (todo-db/list-todos)]
      (is (= (count res) 2))
      (is (= (:content (first res)) (:content test-todo1)))
      (is (= (set (keys (first res))) #{:content :id :created_at :done})))))

(deftest update-todo-query-test
  (testing "User can update a todo"
    (let [params {:content "test todo"}
          todo (todo-db/add-todo! params)
          updated-params {:content "new todo" :done true :id (:id todo)}
          response (todo-db/update-todo! updated-params)]
      (is (= updated-params
             (select-keys response (keys updated-params)))))))
