(ns doit.todo.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [doit.todo.db :as todo-db]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))

(use-fixtures :once fixtures/load-config fixtures/create-migrate-destroy-db fixtures/start-stop-server)
(use-fixtures :each fixtures/isolate-db)

(defn todo-api-end-point []
  (let [{:keys [host port]} (config/webserver)]
    (format "http://%s:%s/api/todo/" host port)))

(defn post-api-call [url body]
  @(http/post
    url
    {:header "Content-Type: application/json"
     :body   (json/write-str body)}))

(defn parse-body [body]
  (json/read-str body :key-fn keyword))

(defn create-todo [content]
  (-> (post-api-call (todo-api-end-point) {:content content})
      (update :body parse-body)))

(defn update-todo [id content]
  (let [url (str (todo-api-end-point) id "/")]
    (-> @(http/put
          url
          {:header "Content-Type: application/json"
           :body   (json/write-str content)})
        (update :body parse-body))))

(defn list-todos []
  (-> @(http/get (todo-api-end-point)) (update :body parse-body)))

(deftest test-todo-crud
  (let [content1        "Test Todo 1"
        content2        "Test Todo 2"
        todo-response-1 (create-todo content1)
        todo-response-2 (create-todo content2)]
    (testing "user can create a todo"
      (is (= (:status todo-response-1) 201))
      (is (= (:status todo-response-2) 201))
      (is (= (set (keys (:body todo-response-1))) #{:content :id :done}))
      (is (= (get-in todo-response-2 [:body :content]) content2)))
    (testing "user can list added todos"
      (let [list-response (list-todos)]
        (is (= 200 (:status list-response)))
        (is (= 2 (count (:body list-response))))
        (is (= #{content1 content2}
               (set (map :content (:body list-response)))))))

    (let [id-1 (get-in todo-response-1 [:body :id])]
      (testing "user can update an added todo"
        (let [updated-data    {:content "new content" :done true :id id-1}
              update-response (update-todo id-1 updated-data)]
          (is (= 200 (:status update-response)))
          (is (= (set (keys (:body update-response))) #{:content :id :done}))
          (is (= (:done updated-data) (get-in update-response [:body :done])))
          (is (= (:content updated-data) (get-in update-response [:body :content])))))
      (testing "user get bad request error on attempting to update todo with bad data"
        (let [bad-updated-data {:cont "new content" :done false}
              {:keys [status]} (update-todo id-1 bad-updated-data)]
          (is (= 400 status)))))))

(deftest test-create-todo-bad-request
  (testing "user gets error on bad request"
    (let [{:keys [status]} (post-api-call (todo-api-end-point) {:ody "Test Todo"})]
      (is (= status 400)))))

(deftest test-update-non-existant
  (testing "user cannot update a non existant todo"
    (let [{:keys [status]} (update-todo 1 {:content "new todo" :done false :id 1})]
      (is (= 404 status)))))
