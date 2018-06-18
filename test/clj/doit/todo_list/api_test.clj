(ns doit.todo-list.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [doit.user.db :as user-db]
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
