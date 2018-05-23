(ns doit.todo.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db fixtures/start-stop-server)
(use-fixtures :each fixtures/isolate-db)

(defn todo-api-end-point []
  (str (config/api-end-point) "todo/"))

(defn create-todo-api-call [body]
  @(http/post
   (todo-api-end-point)
   {:header "Content-Type: application/json"
    :body   (json/write-str body)}))

(defn parse-body [body]
  (json/read-str body :key-fn keyword))

(deftest test-create-todo-api
  (testing "user can create a todo"
    (let [test-content "Test Todo"
          {:keys [status body]} (create-todo-api-call {:content test-content})
          parsed-body (parse-body body)]
      (is (= status 201))
      (is (= (set (keys parsed-body)) #{:content :id}))
      (is (= (:content parsed-body) test-content)))))

(deftest test-create-todo-bad-request
  (testing "returns error on bad request"
    (let [{:keys [status]} (create-todo-api-call {:ody "Test Todo"})]
      (is (= status 400)))))

(deftest test-retrieve-todo-api
  (testing "user can retrieve list of todos"
    (let [test-todo1 {:content "test todo 1"}
          test-todo2 {:content "test todo 2"}
          _ (create-todo-api-call test-todo1)
          _ (create-todo-api-call test-todo2)
          {:keys [status body]} @(http/get (todo-api-end-point))]
      (is (= status 200))
      (let [parsed-body (parse-body body)]
        (is (= (count parsed-body) 2))
        (is (= (:content (first parsed-body))
                 (:content test-todo1)))
        (is (= (set (keys (first parsed-body)))
                 #{:content :id}))))))
