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
  (wrap-response {:client-id (config/google-client-id)} 200))

(defn parse-body [req-body]
  (-> req-body
      (.bytes)
      (slurp)
      (json/read-str :key-fn keyword)))

(defn sign-in* [token]
  (if (auth/token-valid? token)
    (wrap-response "Sign-in successful" 200)
    ;; Maybe I should return user details
    (let [{:keys [status body]} @(http/get (auth/token-info-url token))
          parsed-body (json/read-str body)]
      (if (= status 200)
        (-> {:email (:email parsed-body)
                                 :token token
                                 :token_exp (:exp parsed-body)}
            user-db/create-user!
            (select-keys [:email])
            (wrap-response 201))
        (wrap-response {:error "Sign in unsucessful"} 400)))))
