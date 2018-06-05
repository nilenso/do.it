(ns doit.middleware
  (:require
   [clojure.string :as str]
   [clojure.data.json :as json]))

(defn forbidden-response []
  {:status 403
   :body {:error "Forbidden"}})

(defn parse-body [req-body]
  (-> req-body
      (.bytes)
      (slurp)
      (json/read-str :key-fn keyword)))

(defn wrap-json-request [handler]
  (fn [request]
    (if-let [body (:body request)]
      (handler (assoc request :body (parse-body body)))
      (handler request))))

(defn wrap-json-response [handler]
  (fn [request]
    (let [response (handler request)]
      (merge response
             {:headers {"Content-Type" "application/json"}
              :body (json/write-str (:body response))}))))
