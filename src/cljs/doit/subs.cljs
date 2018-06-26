(ns doit.subs
  (:require [re-frame.core :as rf]))

(defn registrations []
  (rf/reg-sub
   ::todo
   (fn [db [_ id]]
     (get-in db [:todos id])))

  (rf/reg-sub
   ::todos
   (fn [db [_ listid]]
     (filter #(= listid (:listid %)) (vals (:todos db)))))

  (rf/reg-sub
   ::todo-list
   (fn [db [_ id]]
     (get-in db [:todo-lists id])))

  (rf/reg-sub
   ::todo-lists
   (fn [db _]
     (vals (:todo-lists db))))

  (rf/reg-sub
   ::remaining-todos
   (fn [[_ listid] _]
     (rf/subscribe [::todos listid]))

   (fn [todos query-v _]
     (remove :done todos)))

  (rf/reg-sub
   ::completed-todos
   (fn [[_ listid] _]
     (rf/subscribe [::todos listid]))

   (fn [todos query-v _]
     (filter :done todos)))

  (rf/reg-sub
   ::auth-token
   (fn [db _]
     (get-in db [:user :token])))

  (rf/reg-sub
   ::client-id
   (fn [db _]
     (get db :client-id))))

(defn init []
  (registrations))
