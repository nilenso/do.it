(ns doit.api-test
  (:require
   [doit.fixtures :as fixtures]
   [doit.test-helpers :as test-helpers]
   [clojure.test :as t]))

(t/use-fixtures :once fixtures/init! fixtures/migrate-test-db fixtures/serve-app)
(t/use-fixtures :each fixtures/isolate-db)

(t/deftest add-and-retrieve-todo
  (let [todo1 {"body" "first todo"}
        todo2 {"body" "second todo"}]
    (t/testing "User can add a todo"
      (let [{:keys [status body]} (test-helpers/http-request
                                   :post
                                   (str test-helpers/api-root "todo/")
                                   todo1)
            _ (test-helpers/http-request
                                   :post
                                   (str test-helpers/api-root "todo/")
                                   todo2)]
        (t/is (= 201 status))))

    (t/testing "User can retrieve added todo"
      (let [{:keys [status body]} (test-helpers/http-request
                                   :get
                                   (str test-helpers/api-root "todo/"))]
        (t/is (= 2 (count body)))
        (t/is (= 200 status))
        (t/is (= "first todo" (:body (first body))))))))
