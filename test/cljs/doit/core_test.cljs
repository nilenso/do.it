(ns doit.core-test
  (:refer-clojure :exclude [subs])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [doit.events :as events]
            [doit.subs :as subs]
            [doit.auth :as auth]
            [doit.views :as views]))

(defn test-fixtures
  []
  ;; Rewriting ::update-todo event to return the updated todo assuming the update request
  ;; will be successful. This event is used by event ::mark-done and ::mark-undone
  (rf/reg-event-fx
   ::events/update-todo
   (fn [cofx [_ todo]]
     {:db (:db cofx)
      :dispatch [::events/update-todo-success todo]})))

(deftest event-handler-and-subs-test
  (rf-test/run-test-sync
   (test-fixtures)
   (rf/dispatch [::events/initialize-db])

   ;; Assuming http requests made by ::get-todos, ::add-todo succeeds and
   ;; they call the corresponding success events
   (let [backend-todos   [{:content "test todo 1" :done true :id 1}
                          {:content "test todo 2" :done false :id 2}]
         new-todo        {:content "new todo" :done false :id 3}
         new-todo-done   {:content "new todo" :done true :id 3}
         all-todos       (rf/subscribe [::subs/todos])
         completed-todos (rf/subscribe [::subs/completed-todos])
         remaining-todos (rf/subscribe [::subs/remaining-todos])
         auth-token      (rf/subscribe [::subs/auth-token])]

     (testing "Can set auth-token in app-db"
       (rf/dispatch [::auth/save-auth-token "tkn"])
       (is (= "tkn" @auth-token)))

     (testing "user can fetch todos from backend"
       (rf/dispatch [::events/get-todos-success backend-todos])
       (is (= backend-todos (vec @all-todos))))

     (testing "user can add a new todo"
       (rf/dispatch [::events/add-todo-success new-todo])
       (is (= 3 (count @all-todos)))
       (is (contains? (set @all-todos) new-todo))

       (is (= 2 (count @remaining-todos)))
       (is (contains? (set @remaining-todos) new-todo))

       (is (= 1 (count @completed-todos)))
       (is (not (contains? (set @completed-todos) new-todo))))

     (testing "user can mark a remaining todo as done"
       (rf/dispatch [::events/mark-done (:id new-todo)])
       (is (= 1 (count @remaining-todos)))
       (is (= 2 (count @completed-todos)))
       (is (contains? (set @completed-todos) new-todo-done)))

     (testing "user can mark a completed todo as undone"
       (rf/dispatch [::events/mark-undone (:id new-todo-done)])
       (is (= 2 (count @remaining-todos)))
       (is (= 1 (count @completed-todos)))
       (is (contains? (set @remaining-todos) new-todo)))

     (testing "remaining-todos-panel renders all remaining todos"
       (let [rendered-hiccup ((views/remaining-todos-panel))]
         (is (clojure.set/subset? (set (map :content @remaining-todos))
                                  (set (flatten rendered-hiccup))))))

     (testing "completed-todos-panel renders all completed todos"
       (let [rendered-hiccup ((views/completed-todos-panel))]
         (is (clojure.set/subset? (set (map :content @completed-todos))
                                  (set (flatten rendered-hiccup))))))

     (testing "signout removes auth token and todos"
       (rf/dispatch [::auth/sign-out])
       (is (= nil @auth-token))
       (is (= nil @all-todos))))))
