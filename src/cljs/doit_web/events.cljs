(ns doit-web.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [doit-web.db :as db]
            [doit-web.config :as config]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(defn todos-retrieved [db [_ todo-list]]
  (.log js/console "Todos retrieved")
  (assoc db :todos todo-list))

(defn request-failed [db [_ err]]
  (prn "request failed" err)
  (.log js/console (str "Request failed with status" (:status err)))
  db)

(defn get-todos [cofx _]
  {:http-xhrio {:method          :get
                :uri             (str config/srv-url "api/todo/")
                :timeout         10000
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [:todos-retrieved]
                :on-failure      [:request-failed]}})

(defn add-todo [cofx [_ data]]
  {:http-xhrio {:method          :post
                :uri             (str config/srv-url "api/todo/")
                :params          data
                :timeout         10000
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [:get-todos]
                :on-failure      [:request-failed]}})

(rf/reg-event-db :todos-retrieved todos-retrieved)
(rf/reg-event-db :request-failed request-failed)
(rf/reg-event-fx :get-todos get-todos)
(rf/reg-event-fx :add-todo add-todo)

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _] db/default-db))
