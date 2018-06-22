(ns doit.test-utils
  (:require [doit.config :as config]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(defn api-end-point []
  (let [{:keys [host port]} (config/webserver)]
    (format "http://%s:%s/api/" host port)))

(defn post-api-call [url body token]
  @(http/post
    url
    {:headers {"Content-Type"  "application/json"
               "Authorization" (str "Bearer " token)}
     :body    (json/write-str body)}))

(defn put-api-call [url body token]
  @(http/put
    url
    {:headers {"Content-Type"  "application/json"
               "Authorization" (str "Bearer " token)}
     :body    (json/write-str body)}))

(defn parse-body [body]
  (json/read-str body :key-fn keyword))
