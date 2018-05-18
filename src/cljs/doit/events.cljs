(ns doit.events
  (:require [re-frame.core :as rf]
            [doit.db :as db]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(def todo-url "/api/todo/")

(rf/reg-event-db
 ::request-failed
 (fn [db [_ err]]
   (print "Request failed with response" err)
   db))

(rf/reg-event-db
 ::get-todos-success
 (fn [db [_ todos]]
   (assoc db :todos todos)))

(rf/reg-event-fx
 ::add-todo
 (fn [cofx [_ vals]]
   {:http-xhrio {:method :post
                 :uri todo-url
                 :params vals
                 :timeout         8000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::get-todos]
                 :on-failure [::request-failed]}}))

(rf/reg-event-fx
 ::get-todos
 (fn [cofx _]
   {:http-xhrio {:method :get
                 :uri todo-url
                 :timeout         8000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::get-todos-success]
                 :on-failure [::request-failed]}}))


(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))
