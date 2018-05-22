(ns doit.todo.db-test
  (:require [doit.todo.db :as todo-db]
            [clojure.test :refer :all]
            [doit.fixtures :as fixtures]))

(use-fixtures :once fixtures/migrate-destroy-db)
(use-fixtures :each fixtures/isolate-db)

(deftest add-todo-query-test
  (testing "User can add a todo to db"
    (let [vals {:content "test todo"}
          res (todo-db/add-todo! vals)]
      (is (= (count res) 1))
      (is (= (:content (first res)) (:content vals)))
      (is (= (set (keys (first res))) #{:content :id :created_at})))))

(deftest list-todo-query-test
  (testing "User can list the todos"
    (let [test-todo1 {:content "test todo 1"}
          test-todo2 {:content "test todo 2"}
          _ (todo-db/add-todo! test-todo1)
          _ (todo-db/add-todo! test-todo2)
          res (todo-db/list-todos)]
      (is (= (count res) 2))
      (is (= (:content (first res)) (:content test-todo1)))
      (is (= (set (keys (first res))) #{:content :id :created_at})))))
