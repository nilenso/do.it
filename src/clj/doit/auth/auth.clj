(ns doit.auth.auth
  (:require
   [org.httpkit.client :as http]
   [doit.user.db :as user-db]
   [doit.config :as config]
   [doit.util :as util]
   [clojure.data.json :as json]))

(defn token-info-url [token]
  (format "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s" token))

(defn handle-new-token [token]
  "Validates a google OAuth token and creates a user if it is valid.
   Reference: https://developers.google.com/identity/sign-in/web/backend-auth#verify-the-integrity-of-the-id-token"
  (let [{:keys [status body]} @(http/get (token-info-url token))
        parsed-body (json/read-str body :key-fn keyword)]
    (when (and
         (= status 200)
         (util/not-expired? (Integer. (:exp parsed-body)))
         (= (:aud parsed-body) (config/google-client-id)))
      (user-db/create-or-update-user! {:email (:email parsed-body)
                                       :token token
                                       :token_exp (Integer. (:exp parsed-body))}))))

(defn handle-token [token]
  "Validates the authorization token and returns the corresponding user if
   token is valid, returns `nil` otherwise. Creates a new user if one doesn't exist already."
  (if-let [user (user-db/get-user-by-token token)]
    (when (util/not-expired? (:token_exp user)) user)
    (handle-new-token token)))
