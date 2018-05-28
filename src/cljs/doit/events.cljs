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
  (let [todo-id-map (->> (map (fn [t] [(:id t) t]) todos)
                         (into {}))]
    (assoc db :todos todo-id-map)))

(rf/reg-event-db
 ::get-todos-success
 [db-spec-inspector]
 get-todo-success)

(defn add-todo-success
  [db [_ todo]]
  (assoc-in db [:todos (:id todo)] todo))

(rf/reg-event-db
 ::add-todo-success
 [db-spec-inspector]
 add-todo-success)

(defn update-todo-success
  [db [_ todo]]
  (assoc-in db [:todos (:id todo)] todo))

(rf/reg-event-db
 ::update-todo-success
 [db-spec-inspector]
 update-todo-success)

(rf/reg-event-fx
 ::add-todo
 (fn [cofx [_ vals]]
   {:http-xhrio {:method :post
                 :uri todo-url
                 :params vals
                 :timeout         8000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::add-todo-success]
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

(rf/reg-event-fx
 ::update-todo
 (fn [cofx [_ todo]]
   (let [url (str todo-url (:id todo) "/")]
     {:http-xhrio {:method          :put
                   :uri             url
                   :params          todo
                   :timeout         8000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::update-todo-success]
                   :on-failure      [::request-failed]}})))

(defn mark-done
  [cofx [_ id]]
  (let [db          (:db cofx)
        todo        (get-in db [:todos id])
        updated-todo (assoc todo :done true)]
    {:db       (:db cofx)
     :dispatch [::update-todo updated-todo]}))

(rf/reg-event-fx
 ::mark-done
 mark-done)

(defn mark-undone
  [cofx [_ id]]
  (let [db          (:db cofx)
        todo        (get-in db [:todos id])
        updated-todo (assoc todo :done false)]
    {:db       (:db cofx)
     :dispatch [::update-todo updated-todo]}))

(rf/reg-event-fx
 ::mark-undone
 mark-undone)

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))
