(ns doit.auth.handler
  (:require
   [clojure.data.json :as json]
   [doit.auth.auth :as auth]
   [doit.config :as config]
   [org.httpkit.client :as http]
   [doit.user.db :as user-db]))

(defn wrap-response [data status]
  {:status status
   :headers {"Content-Type" "application/json"}
   :body (json/write-str data)})

(defn client-id [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (config/google-client-id)})
