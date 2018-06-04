(ns doit.auth.handler
  (:require
   [clojure.data.json :as json]
   [doit.config :as config]))

(defn client-id [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (config/google-client-id)})
