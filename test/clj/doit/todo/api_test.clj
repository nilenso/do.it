(ns doit.todo.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [doit.todo.db :as todo-db]
             [doit.user.db :as user-db]
             [doit.util :as util]
             [doit.test-utils :as test-utils]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db fixtures/start-stop-server)
(use-fixtures :each fixtures/isolate-db)

(defn todo-api-end-point []
  (str (test-utils/api-end-point) "todo/"))

(defn create-todo [params token]
  (-> (test-utils/post-api-call (todo-api-end-point) params token)
      (update :body test-utils/parse-body)))

(defn update-todo [id params token]
  (let [url (str (todo-api-end-point) id "/")]
    (-> (test-utils/put-api-call url params token)
        (update :body test-utils/parse-body))))

(defn delete-todo [id token]
  (let [url (str (todo-api-end-point) id "/")]
    (-> @(http/delete
          url
          {:headers {"Content-Type"  "application/json"
                     "Authorization" (str "Bearer " token)}}))))

(defn list-todos [token]
  (-> @(http/get
        (todo-api-end-point)
        {:headers {"Authorization" (str "Bearer " token)}})
      (update :body test-utils/parse-body)))

(defn logout [token]
  @(http/post
    (str (test-utils/api-end-point) "auth/logout/")
    {:headers {"Content-Type"  "application/json"
               "Authorization" (str "Bearer " token)}}))

(deftest test-todo-crud
  (let [token           "tk1"
        user            (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
        content1        {:content "Test Todo 1" :listid 0}
        content2        {:content "Test Todo 2" :listid 0}
        todo-response-1 (create-todo content1 token)
        todo-response-2 (create-todo content2 token)]

    (testing "user can create a todo"
      (is (= (:status todo-response-1) 201))
      (is (= (:status todo-response-2) 201))
      (is (= (set (keys (:body todo-response-1))) #{:content :id :done :listid}))
      (is (= (get-in todo-response-2 [:body :content]) (:content content2))))

    (testing "user gets error on creating todo with bad keys"
      (let [{:keys [status]} (test-utils/post-api-call (todo-api-end-point) {:ody "Test Todo"} token)]
        (is (= status 400))))

    (testing "user with bad token cannot access todos"
      (let [list-response (list-todos "tk-bad")]
        (is (= 403 (:status list-response)))))

    (testing "user can list added todos"
      (let [list-response (list-todos token)]
        (is (= 200 (:status list-response)))
        (is (= 2 (count (:body list-response))))
        (is (= (set (map :content [content1 content2]))
               (set (map :content (:body list-response)))))))

    (let [id-1 (get-in todo-response-1 [:body :id])]
      (testing "user can update an added todo"
        (let [updated-data    {:content "new content" :done true :id id-1 :listid 0}
              update-response (update-todo id-1 updated-data token)]
          (is (= 200 (:status update-response)))
          (is (= (set (keys (:body update-response))) #{:content :id :done :listid}))
          (is (= (:done updated-data) (get-in update-response [:body :done])))
          (is (= (:content updated-data) (get-in update-response [:body :content])))))

      (testing "user get bad request error on attempting to update todo with bad data"
        (let [bad-updated-data {:cont "new content" :done false}
              {:keys [status]} (update-todo id-1 bad-updated-data token)]
          (is (= 400 status)))))))


(deftest test-delete-todo
  (testing "user can delete a todo"
    (let [token           "tk1"
          user            (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
          {:keys [id]}    (todo-db/add! {:content "some value"})
          delete-response (delete-todo id token)
          list-response   (list-todos token)]
      (is (= 204 (:status delete-response)))
      (is (= 0 (count (:body list-response)))))))

(deftest test-logout
  (testing "user can logout"
    (let [token           "tk1"
          user            (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
          logout-response (logout token)
          list-response   (list-todos token)]
      (is (= 200 (:status logout-response)))
      (is (= 403 (:status list-response))))))

(deftest test-update-non-existant
  (testing "user cannot update a non existant todo"
    (let [token            "tk1"
          user             (user-db/create! {:email "test@nilenso.com" :token token :token_exp 1802444800})
          {:keys [status]} (update-todo 1 {:content "new todo" :done false :id 1} token)]
      (is (= 404 status)))))
