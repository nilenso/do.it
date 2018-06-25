(ns doit.core-test
  (:refer-clojure :exclude [subs])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [doit.core :as core]
            [doit.events :as events]
            [doit.subs :as subs]
            [doit.auth :as auth]
            [doit.views :as views]))

(defn base-fixtures []
  (events/registrations)
  (subs/registrations)
  (auth/registrations)

  (rf/dispatch [::events/initialize-db]))

(defn list-fixtures []
  (rf/dispatch [::events/get-todo-lists-success [{:name "default list" :id 0}]]))

(deftest auth-test
  (rf-test/run-test-sync
   (base-fixtures)

   (let [auth-token (rf/subscribe [::subs/auth-token])]
     (testing "Can set auth-token in app-db"
       (rf/dispatch [::auth/save-auth-token "tkn"])
       (is (= "tkn" @auth-token)))

     (testing "signout removes auth token and todos"
       (rf/dispatch [::auth/sign-out])
       (is (= nil @auth-token))))))


(deftest todo-list-crud-test
  (rf-test/run-test-sync
   (base-fixtures)

   (let [listid-0      0
         listid-1      1
         backend-lists [{:id listid-0 :name "default"}]
         new-list      {:id listid-1 :name "new list"}
         updated-list  {:id listid-1 :name "new updated list"}
         todo-lists    (rf/subscribe [::subs/todo-lists])
         all-todos     (rf/subscribe [::subs/todos 0])]

     (testing "User can fetch todo-lists from backend"
       (rf/dispatch [::events/get-todo-lists-success backend-lists])
       (is (= backend-lists (vec @todo-lists))))

     (testing "User can add a new todo-list"
       (rf/dispatch [::events/add-todo-list-success new-list])
       (is (= 2 (count @todo-lists)))
       (is (contains? (set @todo-lists) new-list)))

     (testing "User can update a todo-list"
       (rf/dispatch [::events/update-todo-list-success updated-list])
       (is (= 2 (count @todo-lists)))
       (is (contains? (set @todo-lists) updated-list)))

     (testing "User can add todos to a list"
       (rf/dispatch [::events/add-todo-success
                     {:id 1 :content "backend list todo" :done false :listid (:id (first backend-lists))}])
       (rf/dispatch [::events/add-todo-success
                     {:id 2 :content "new list todo" :done false :listid (:id new-list)}])
       (is (= 1 (count @(rf/subscribe [::subs/todos listid-0]))))
       (is (= 1 (count @(rf/subscribe [::subs/todos listid-1])))))

     (testing "User can delete a todo-list"
       (rf/dispatch [::events/delete-todo-list-success listid-1])
       (is (= 1 (count @todo-lists)))
       (is (not (contains? (set @todo-lists) updated-list))))

     (testing "Deleting a list deletes corresponding todos"
       (is (= 1 (count @(rf/subscribe [::subs/todos listid-0]))))
       (is (= 0 (count @(rf/subscribe [::subs/todos listid-1]))))))))


(deftest todo-crud-test
  (rf-test/run-test-sync
   (base-fixtures)
   (list-fixtures)

   ;; Rewriting ::update-todo event to return the updated todo assuming the update request
   ;; will be successful. This event is used by event ::mark-done and ::mark-undone
   (rf/reg-event-fx
    ::events/update-todo
    (fn [cofx [_ todo]]
      {:db       (:db cofx)
       :dispatch [::events/update-todo-success todo]}))

   ;; Assuming http requests made by ::get-todos, ::add-todo succeeds and
   ;; they call the corresponding success events
   (let [listid          (:id (first @(rf/subscribe [::subs/todo-lists])))
         backend-todos   [{:content "test todo 1" :done true :id 1 :listid listid}
                          {:content "test todo 2" :done false :id 2 :listid listid}]
         new-todo        {:content "new todo" :done false :id 3 :listid listid}
         new-todo-done   {:content "new todo" :done true :id 3 :listid listid}
         all-todos       (rf/subscribe [::subs/todos listid])
         completed-todos (rf/subscribe [::subs/completed-todos listid])
         remaining-todos (rf/subscribe [::subs/remaining-todos listid])]

     (testing "User can fetch todos from backend"
       (rf/dispatch [::events/get-todos-success backend-todos])
       (is (= backend-todos (vec @all-todos))))

     (testing "User can add a new todo"
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

     (testing "user can delete a todo"
       (rf/dispatch [::events/delete-todo-success (:id new-todo)])
       (is (not (contains? (set (map :id @all-todos)) (:id new-todo))))))))
