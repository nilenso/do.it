(ns doit.todo.handlers
  (:require
   [doit.todo.db :as todo-db]
   [doit.util :as util]
   [doit.web.util :as web-util]
   [ring.util.response :as res]))

(defn create
  [request]
  (if-let [created-todo (todo-db/create! util/connection (:body request))]
    (-> (res/response {:msg "Todo created successfuly"})
        (res/status 201))
    web-util/error-bad-request))

(defn retrieve-all [request]
  (res/response (todo-db/retrieve-all util/connection)))
