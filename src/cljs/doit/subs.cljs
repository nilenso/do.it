(ns doit.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::todos
 (fn [db _]
   (:todos db)))

(rf/reg-sub
 ::remaining-todos
 (fn [query-v _]
   (rf/subscribe [::todos]))

 (fn [todos query-v _]
   (remove :done (vals todos))))

(rf/reg-sub
 ::completed-todos
 (fn [query-v _]
   (rf/subscribe [::todos]))

 (fn [todos query-v _]
   (filter :done (vals todos))))
