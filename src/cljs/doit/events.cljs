(ns doit.events
  (:require [re-frame.core :as rf]
            [doit.db :as db]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [doit.spec :as spec]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(def todo-url "/api/todo/")

(def db-spec-inspector
  (let [check-db-spec (fn [context db]
                        (if (s/valid? ::spec/app-db db)
                          context
                          (throw (s/explain-str ::spec/app-db db))))]
    (rf/->interceptor
     :id :db-spec-inspector
     :before (fn [context]
               (check-db-spec context (get-in context [:coeffects :db])))
     :after (fn [context]
              (check-db-spec context (get-in context [:effects :db]))))))

(rf/reg-event-db
 ::request-failed
 [db-spec-inspector]
 (fn [db [_ err]]
   (print "Request failed with response" err)
   db))

(defn get-todo-success
  [db [_ todos]]
  (let [todo-id-map (reduce (fn [m t] (assoc m (:id t) t))
                            {}
                            todos)]
    (assoc db :todos todo-id-map)))

(rf/reg-event-db
 ::get-todos-success
 [db-spec-inspector]
 get-todo-success)

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
