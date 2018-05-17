(ns doit.todo.db-test
  (:require [doit.todo.db :as db]
            [clojure.test :refer :all]))

(deftest add-todo-query-test
  (testing "User can add a todo to db"
    (let [vals {:content "test todo"}
          res (db/add-todo! vals)]
      (is (= (count res) 1))
      (is (= (:content (first res)) (:content vals)))
      (is (= (set (keys (first res))) #{:content :id :created_at})))))
