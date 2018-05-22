(ns doit.todo.api-test
  (:require  [clojure.test :as t]
             [doit.fixtures :as fixtures]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))

(def todo-api-end-point (str config/rest-api-end-point "todo/"))
(t/use-fixtures :once fixtures/start-stop-server fixtures/migrate-destroy-db)
(t/use-fixtures :each fixtures/isolate-db)

(defn create-todo-api-call [body]
  @(http/post
   todo-api-end-point
   {:header "Content-Type: application/json"
    :body   (json/write-str body)}))

(defn parse-body [body]
  (json/read-str body :key-fn keyword))

(t/deftest test-create-todo-api
  (t/testing "user can create a todo"
    (let [test-content "Test Todo"
          {:keys [status body]} (create-todo-api-call {:content test-content})
          parsed-body (parse-body body)]
      (t/is (= status 201))
      (t/is (= (set (keys parsed-body)) #{:content :id}))
      (t/is (= (:content parsed-body) test-content)))))

(t/deftest test-create-todo-bad-request
  (t/testing "returns error on bad request"
    (let [{:keys [status]} (create-todo-api-call {:ody "Test Todo"})]
      (t/is (= status 400)))))

(t/deftest test-retrieve-todo-api
  (t/testing "user can retrieve list of todos"
    (let [test-todo1 {:content "test todo 1"}
          test-todo2 {:content "test todo 2"}
          _ (create-todo-api-call test-todo1)
          _ (create-todo-api-call test-todo2)
          {:keys [status body]} @(http/get
                                  todo-api-end-point
                                  {:header "Content-Type: application/json"
                                   :body   (json/write-str
                                            {:content "Test Todo"})})
          parsed-body (parse-body body)]
      (t/is (= status 200))
      (t/is (= (count parsed-body) 2))
      (t/is (= (:content (first parsed-body))
               (:content test-todo1)))
      (t/is (= (set (keys (first parsed-body)))
               #{:content :id})))))
