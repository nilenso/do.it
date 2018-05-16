(ns doit.test-api
  (:require  [clojure.test :as t]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))


(def todo-api-end-point (str config/api-end-point "todo/"))

(t/deftest test-create-todo-api
  (t/testing "user can create a todo"
    (let [{:keys [status]} @(http/post
                             todo-api-end-point
                             {:header "Content-Type: application/json"
                              :body   (json/write-str
                                       {:body "Test Todo"})})]
      (t/is (= status 201)))))

(t/deftest test-retrieve-todo-api
  (t/testing "user can retrieve list of todos"
      (let [{:keys [status body]} @(http/get
                                    todo-api-end-point
                                    {:header "Content-Type: application/json"
                                     :body   (json/write-str
                                              {:body "Test Todo"})})]
        (t/is (= status 200)))))
