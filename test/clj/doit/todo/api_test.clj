(ns doit.todo.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [doit.user.db :as user-db]
             [doit.auth.auth :as auth]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db fixtures/start-stop-server)
(use-fixtures :each fixtures/isolate-db)

(defn api-end-point []
  (let [{:keys [host port]} (config/webserver)]
    (format "http://%s:%s/api/" host port)))

(defn todo-api-end-point []
 (str (api-end-point) "todo/"))

(defn post-api-call [url body token]
  @(http/post
    url
    {:headers {"Content-Type"  "application/json"
              "Authorization" (str "Bearer " token)}
     :body   (json/write-str body)}))

(defn parse-body [body]
  (json/read-str body :key-fn keyword))

(defn create-todo [content token]
  (-> (post-api-call (todo-api-end-point) {:content content} token)
      (update :body parse-body)))

(defn update-todo [id content token]
  (let [url (str (todo-api-end-point) id "/")]
    (-> @(http/put
          url
          {:headers {"Content-Type" "application/json"
                     "Authorization" (str "Bearer " token)}
           :body   (json/write-str content)})
        (update :body parse-body))))

(defn list-todos [token]
  (-> @(http/get
        (todo-api-end-point)
        {:headers {"Authorization" (str "Bearer " token)}})
      (update :body parse-body)))

(defn logout [token]
  @(http/post
    (str (api-end-point) "auth/logout/")
    {:headers {"Content-Type" "application/json"
                     "Authorization" (str "Bearer " token)}}))

(deftest test-todo-crud
  (let [token "tk1"
        user (user-db/create-user! {:email "test@nilenso.com" :token token :token_exp (+ 100 (auth/current-unix-time))})
        content1        "Test Todo 1"
        content2        "Test Todo 2"
        todo-response-1 (create-todo content1 token)
        todo-response-2 (create-todo content2 token)]
    (testing "user can create a todo"
      (is (= (:status todo-response-1) 201))
      (is (= (:status todo-response-2) 201))
      (is (= (set (keys (:body todo-response-1))) #{:content :id :done}))
      (is (= (get-in todo-response-2 [:body :content]) content2)))

    (testing "user gets error on creating todo with bad keys"
      (let [{:keys [status]} (post-api-call (todo-api-end-point) {:ody "Test Todo"} token)]
        (is (= status 400))))

    (testing "user with bad token cannot access todos"
      (let [list-response (list-todos "tk-bad")]
        (is (= 403 (:status list-response)))))

    (testing "user can list added todos"
      (let [list-response (list-todos token)]
        (is (= 200 (:status list-response)))
        (is (= 2 (count (:body list-response))))
        (is (= #{content1 content2}
               (set (map :content (:body list-response)))))))

    (let [id-1 (get-in todo-response-1 [:body :id])]
      (testing "user can update an added todo"
        (let [updated-data    {:content "new content" :done true :id id-1}
              update-response (update-todo id-1 updated-data token)]
          (is (= 200 (:status update-response)))
          (is (= (set (keys (:body update-response))) #{:content :id :done}))
          (is (= (:done updated-data) (get-in update-response [:body :done])))
          (is (= (:content updated-data) (get-in update-response [:body :content])))))
      (testing "user get bad request error on attempting to update todo with bad data"
        (let [bad-updated-data {:cont "new content" :done false}
              {:keys [status]} (update-todo id-1 bad-updated-data token)]
          (is (= 400 status)))))))

(deftest test-logout
  (testing "user can logout"
    (let [token "tk1"
          user (user-db/create-user! {:email "test@nilenso.com" :token token :token_exp (+ 100 (auth/current-unix-time))})
          logout-response (logout token)
          list-response (list-todos token)]
      (is (= 200 (:status logout-response)))
      (is (= 403 (:status list-response))))))

(deftest test-update-non-existant
  (testing "user cannot update a non existant todo"
    (let [token "tk1"
          user (user-db/create-user! {:email "test@nilenso.com" :token token :token_exp 1802444800})
          {:keys [status]} (update-todo 1 {:content "new todo" :done false :id 1} token)]
      (is (= 404 status)))))
