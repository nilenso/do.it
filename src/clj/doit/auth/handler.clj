(ns doit.auth.handler
  (:require
   [clojure.data.json :as json]
   [doit.auth.auth :as auth]
   [doit.config :as config]
   [org.httpkit.client :as http]
   [doit.util :as util]
   [doit.user.db :as user-db]))

(defn client-id [request]
  (util/response* {:client-id (config/google-client-id)} 200))

(defn logout [request]
  (let [user-id (get request :user-id)]
    (user-db/update! {:id user-id :token nil :token_exp nil})))

(defn invite! [request]
  (let [user-id    (get request :user-id)
        invited-by (:email (user-db/get-by-id user-id))
        email      (get-in request [:body :email])]
    (util/response* {:email (auth/invite-user! email invited-by)} 200)))

(defn verify-token [request]
  (let [token  (get-in request [:body :token])
        valid? (auth/verify-token token)]
    (if valid?
      {:status 200}
      {:status 403})))
