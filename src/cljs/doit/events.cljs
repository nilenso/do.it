(ns doit.events
  (:require [re-frame.core :as rf]
            [doit.db :as db]
            [doit.ws :as ws]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
            [wscljs.client :as ws-client]
            [wscljs.format :as fmt]))

(def todo-url "/api/todo/")

(rf/reg-event-db
 ::request-failed
 (fn [db [_ err]]
   (print "Request failed with response" err)
   db))

(defn get-todo-success
  [db [_ todos]]
  (assoc db :todos todos))

(rf/reg-event-db
 ::get-todos-success
 get-todo-success)

(rf/reg-event-fx
 ::trigger-add-todo
 (fn [cofx [_ vals]]
   {:ws-send {:command "add"
              :data vals}}))

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

(rf/reg-fx
 :ws-send
 (fn [data]
   (ws-client/send @ws/socket data fmt/json)))
