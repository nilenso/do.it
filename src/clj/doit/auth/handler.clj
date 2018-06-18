(ns doit.auth.handler
  (:require
   [clojure.data.json :as json]
   [doit.auth.auth :as auth]
   [doit.config :as config]
   [org.httpkit.client :as http]
   [doit.user.db :as user-db]))

(defn wrap-response [data status]
  {:status status
   :body data})

(defn client-id [request]
  (wrap-response {:client-id (config/google-client-id)} 200))

(defn logout [request]
  (let [user-id (get request :user-id)]
    (user-db/update! {:id user-id :token nil :token_exp nil})))
