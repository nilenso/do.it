(ns doit.todo.db-test
  (:require [doit.todo.db :as todo-db]
            [clojure.test :refer :all]
            [doit.fixtures :as fixtures]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db)
(use-fixtures :each fixtures/isolate-db)

(deftest add-todo-query-test
  (testing "User can add a todo to db"
    (let [params   {:content "test todo"}
          response (todo-db/add! params)]
      (is (= (:content response) (:content params)))
      (is (= (:done response) false))
      (is (= (set (keys response)) #{:content :id :created_at :done :listid})))))

(deftest list-todos-query-test
  (testing "User can list the todos"
    (let [test-todo1 {:content "test todo 1"}
          test-todo2 {:content "test todo 2"}
          _          (todo-db/add! test-todo1)
          _          (todo-db/add! test-todo2)
          res        (todo-db/list-all)]
      (is (= (count res) 2))
      (is (= (:content (first res)) (:content test-todo1)))
      (is (= (set (keys (first res))) #{:content :id :created_at :done :listid})))))

(deftest update-todo-query-test
  (testing "User can update a todo"
    (let [params         {:content "test todo"}
          todo           (todo-db/add! params)
          updated-params {:content "new todo" :done true :id (:id todo)}
          response       (todo-db/update! updated-params)]
      (is (= updated-params
             (select-keys response (keys updated-params)))))))

(deftest delete-todo-query-test
  (testing "User can delete a todo"
    (let [{:keys [id]} (todo-db/add! {:content "some content"})
          response     (todo-db/delete! id)]
      (is (= 1 (first response)))))

  (testing "Deleting a non-existent todo deletes nothing"
    (let [response (todo-db/delete! 10)]
      (is (= 0 (first response))))))
