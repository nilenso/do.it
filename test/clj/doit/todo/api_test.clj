(ns doit.todo.api-test
  (:require  [clojure.test :refer :all]
             [doit.fixtures :as fixtures]
             [doit.todo.db :as todo-db]
             [org.httpkit.client :as http]
             [clojure.data.json :as json]
             [doit.config :as config]))

(use-fixtures :once fixtures/load-config fixtures/migrate-destroy-db fixtures/start-stop-server)
(use-fixtures :each fixtures/isolate-db)

(defn todo-api-end-point []
  (str (config/api-end-point) "todo/"))

(defn post-api-call [url body]
  @(http/post
    url
    {:header "Content-Type: application/json"
     :body   (json/write-str body)}))

(defn parse-body [body]
  (json/read-str body :key-fn keyword))

(deftest test-create-todo-api
  (testing "user can create a todo"
    (let [test-content          "Test Todo"
          {:keys [status body]} (post-api-call (todo-api-end-point) {:content test-content})
          parsed-body           (parse-body body)]
      (is (= status 201))
      (is (= (set (keys parsed-body)) #{:content :id :done}))
      (is (= (:content parsed-body) test-content)))))

(deftest test-create-todo-bad-request
  (testing "user gets error on bad request"
    (let [{:keys [status]} (post-api-call (todo-api-end-point) {:ody "Test Todo"})]
      (is (= status 400)))))

(deftest test-retrieve-todo-api
  (testing "user can retrieve list of todos"
    (let [test-todo1            {:content "test todo 1"}
          test-todo2            {:content "test todo 2"}
          _                     (todo-db/add-todo! test-todo1)
          _                     (todo-db/add-todo! test-todo2)
          {:keys [status body]} @(http/get (todo-api-end-point))]
      (is (= status 200))
      (let [parsed-body (parse-body body)]
        (is (= (count parsed-body) 2))
        (is (= (:content (first parsed-body))
               (:content test-todo1)))
        (is (= (set (keys (first parsed-body)))
               #{:content :id :done}))))))

(deftest test-mark-done-todo-api
  (testing "user can mark a todo as done"
    (let [test-todo             {:content "test todo"}
          {:keys [id]}          (todo-db/add-todo! test-todo)
          url                   (format "%s%s/mark_done/" (todo-api-end-point) id)
          {:keys [status body]} @(http/post url)]
      (is (= status 200))
      (let [parsed-body (parse-body body)]
        (is (= (set (keys parsed-body))
               #{:content :id :done}))
        (is (= (:done parsed-body) true))
        (is (= (:content parsed-body)
               (:content test-todo)))))))

(deftest test-mark-done-todo-bad-id-api
  (testing "user gets error on marking a non existant todo as done"
    (let [url              (format "%s%s/mark_done/" (todo-api-end-point) 11)
          {:keys [status]} @(http/post url)]
      (is (= status 404)))))

(deftest test-mark-undone-todo-api
  (testing "user can unmark a marked todo as notdone"
    (let [test-todo             {:content "test todo"}
          {:keys [id]}          (todo-db/add-todo! test-todo)
          _                     (todo-db/mark-done! (Integer. id))
          url                   (format "%s%s/mark_undone/" (todo-api-end-point) id)
          {:keys [status body]} @(http/post url)]
      (is (= status 200))
      (let [parsed-body (parse-body body)]
        (is (= (set (keys parsed-body))
               #{:content :id :done}))
        (is (= (:done parsed-body) false))
        (is (= (:content parsed-body)
               (:content test-todo)))))))

(deftest test-mark-undone-todo-bad-id-api
  (testing "user gets error on marking a non existant todo as undone"
    (let [url              (format "%s%s/mark_undone/" (todo-api-end-point) 11)
          {:keys [status]} @(http/post url)]
      (is (= status 404)))))
