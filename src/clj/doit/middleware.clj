(ns doit.middleware
  (:require
   [doit.auth.auth :as auth]
   [clojure.string :as str]
   [clojure.data.json :as json]))

(defn forbidden-response []
  {:status 403
   :body {:error "Forbidden"}})

(defn wrap-token [handler]
  (fn [request]
    (if-let [auth-header (get-in request [:headers "authorization"])]
      (let [[type token] (str/split auth-header #" ")]
        (if-let [user (auth/handle-token token)]
          (handler (assoc request :user-id (:id user)))
          (forbidden-response)))
      (forbidden-response))))

(defn- parse-body [req-body]
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
