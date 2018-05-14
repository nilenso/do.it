(ns doit.test-helpers
  (:require
   [doit.util :as util]
   [cheshire.core :as json]
   [org.httpkit.client :as http]
   [clojure.walk :refer [keywordize-keys]]))

(def api-root (str "http://localhost:" (util/from-config :port) "/api/"))

(defn http-request-raw
  "Makes a HTTP request. Does not process the body."
  ([method url] (http-request-raw method url nil))
  ([method url body]
   (let [params (merge {:url     url
                        :method  method
                        :headers {"Content-Type" "application/json"}
                        :as      :text}
                       (if body {:body (json/encode body)}))]
     @(http/request params))))

(defn http-request
  "Makes a HTTP request. Decodes the JSON body."
  [& args]
  (let [response (apply http-request-raw args)]
    (assoc response :body (keywordize-keys (json/decode (:body response))))))
