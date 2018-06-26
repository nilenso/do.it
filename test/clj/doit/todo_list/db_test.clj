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
      (is (= (set (keys response)) #{:name :id :archived})))))

(deftest list-todo-lists-query-test
  (testing "User can list the todo lists"
    (let [new-list {:name "new list"}
          _        (todo-list-db/create! new-list)
          response (todo-list-db/list-all)]
      (is (= (count response) 2)) ;; The default list is also present
      (is (= (:name (last response)) (:name new-list)))
      (is (= (set (keys (last response))) #{:name :id :archived})))))

(deftest edit-todo-list-test
  (testing "User can edit the name of a todo-list"
    (let [params         {:name "test list"}
          todo-list      (todo-list-db/create! params)
          updated-params {:name "updated test list" :archived false}
          response       (todo-list-db/update! updated-params)]
      (is (= response (select-keys response (keys updated-params))))))

  (testing "User can archive a todo-list"
    (let [params         {:name "test list"}
          todo-list      (todo-list-db/create! params)
          updated-params {:name "test list" :archived true}
          response       (todo-list-db/update! updated-params)]
      (is (= response (select-keys response (keys updated-params)))))))

(deftest delete-todo-list-test
  (testing "User can delete a todo-list"
    (let [params    {:name "test list"}
          todo-list (todo-list-db/create! params)
          response  (todo-list-db/delete! (:id todo-list))]
      (is (= 1 (first response)))))

  (testing "Deleting a non-existent todo-list deletes nothing"
    (let [response (todo-list-db/delete! 10)]
      (is (= 0 (first response))))))
