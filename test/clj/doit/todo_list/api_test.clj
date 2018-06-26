(ns doit.todo-list.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [doit.user.db :as user-db]
             [doit.todo-list.db :as todo-list-db]
             [doit.util :as util]
             [doit.test-utils :as test-utils]
             [org.httpkit.client :as http]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db fixtures/start-stop-server)
(use-fixtures :each fixtures/isolate-db)

(defn todo-list-api-end-point []
  (str (test-utils/api-end-point) "todo-list/"))

(defn create! [params token]
  (-> (test-utils/post-api-call (todo-list-api-end-point) params token)
      (update :body test-utils/parse-body)))

(defn list-all [token]
  (-> @(http/get
        (todo-list-api-end-point)
        {:headers {"Authorization" (str "Bearer " token)}})
      (update :body test-utils/parse-body)))

(defn update! [id params token]
  (let [update-url (str (todo-list-api-end-point) id "/")]
    (-> (test-utils/put-api-call update-url params token)
        (update :body test-utils/parse-body))))

(defn delete! [id token]
  (let [delete-url (str (todo-list-api-end-point) id "/")]
    @(http/delete
      delete-url
      {:headers {"Authorization" (str "Bearer " token)}})))


(deftest test-list
  (let [token         "tk1"
        user          (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
        list-response (list-all token)]
    (testing "User can retrieve todo lists"
      (is (= (:status list-response) 200))
      ;; NOTE: A default list is always present in the db
      (is (= 1 (count (:body list-response)))))))

(deftest test-create
  (let [token             "tk1"
        user              (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
        create-params-1   {:name "test list 1"}
        create-params-2   {:name "test list 2"}
        create-response-1 (create! create-params-1 token)
        create-response-2 (create! create-params-2 token)]
    (is (= 201 (:status create-response-1)))
    (is (= 201 (:status create-response-2)))
    (is (= (set (keys (:body create-response-1))) #{:name :id}))
    (is (= (get-in create-response-1 [:body :name]) (:name create-params-1)))))

(deftest test-update
  (testing "User can update a todo"
    (let [token         "tk1"
          user          (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
          todo-list     (todo-list-db/create! {:name "test list 1"})
          update-params {:name "updated test list" :id (:id todo-list)}
          response      (update! (:id todo-list) update-params token)]
      (is (= 200 (:status response)))
      (is (= #{:name :id} (set (keys (:body response)))))
      (is (= (:name update-params) (get-in response [:body :name]))))))

(deftest test-delete
  (testing "User can delete a todo"
    (let [token           "tk1"
          user            (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
          todo-list-id    (:id (todo-list-db/create! {:name "new list"}))
          delete-response (delete! todo-list-id token)
          all-todo-lists  (todo-list-db/list-all)]
      (is (= 204 (:status delete-response)))
      (is (not (contains? (set (map :id all-todo-lists))
                          todo-list-id))))))

(deftest test-delete-nonexistant
  (testing "Deleting a non existant todo-list does nothing"
    (let [token           "tk1"
          user            (user-db/create! {:email "test@nilenso.com" :token token :token_exp (+ 100 (util/current-unix-time))})
          count-before    (count (todo-list-db/list-all))
          delete-response (delete! 12 token)
          count-after     (count (todo-list-db/list-all))]
      (is (= 204 (:status delete-response)))
      (is (= count-before count-after)))))
