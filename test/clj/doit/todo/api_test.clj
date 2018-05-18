(ns doit.todo.api-test
  (:require  [clojure.test :as t]
             [doit.fixtures :as fixtures]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))

(def todo-api-end-point (str config/api-end-point "todo/"))
(t/use-fixtures :once fixtures/start-stop-server fixtures/migrate-destroy-db)
(t/use-fixtures :each fixtures/isolate-db)


(t/deftest test-create-todo-api
  (t/testing "user can create a todo"
    (let [test-content "Test Todo"
          {:keys [status body]} @(http/post
                                  todo-api-end-point
                                  {:header "Content-Type: application/json"
                                   :body   (json/write-str
                                            {:content test-content})})
          parsed-body (clojure.walk/keywordize-keys (json/read-str body))]
      (t/is (= status 201))
      (t/is (= (set (keys parsed-body)) #{:content :id}))
      (t/is (= (:content parsed-body) test-content)))))

(t/deftest test-create-todo-bad-request
  (t/testing "returns error on bad request"
    (let [{:keys [status]} @(http/post
                             todo-api-end-point
                             {:header "Content-Type: application/json"
                              :body   (json/write-str
                                       {:ody "Test Todo"})})]
      (t/is (= status 400)))))

(t/deftest test-retrieve-todo-api
  (t/testing "user can retrieve list of todos"
    (let [{:keys [status body]} @(http/get
                                  todo-api-end-point
                                  {:header "Content-Type: application/json"
                                   :body   (json/write-str
                                            {:content "Test Todo"})})]
      (t/is (= status 200)))))
