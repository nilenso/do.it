(ns doit.auth.auth
  (:require
   [org.httpkit.client :as http]
   [doit.user.db :as user-db]
   [doit.config :as config]
   [doit.util :as util]
   [mailgun.mail :as mail]
   [clojure.data.json :as json]))

(defn token-info-url [token]
  (format "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s" token))

(defn get-token-info [token]
  (let [{:keys [status body]} @(http/get (token-info-url token))]
    (when (= status 200)
      (json/read-str body :key-fn keyword))))

(defn verify-token [token]
  "Returns token info if the token is valid otherwise nil"
  (when-let [token-info (get-token-info token)]
    (when (and
           (util/not-expired? (Integer. (:exp token-info)))
           (= (:aud token-info) (config/google-client-id))
           (user-db/get-by-email (:email token-info)))
      token-info)))

(defn handle-new-token [token]
  "Validates a google OAuth token and updates the token user if the user already exists in the db
   Reference: https://developers.google.com/identity/sign-in/web/backend-auth#verify-the-integrity-of-the-id-token"
  (when-let [token-info (verify-token token)]
    (user-db/update! {:email     (:email token-info)
                      :token     token
                      :token_exp (Integer. (:exp token-info))})))

(defn handle-token [token]
  "Validates the authorization token and returns the corresponding user if
   token is valid, returns `nil` otherwise."
  (if-let [user (user-db/get-by-token token)]
    (when (util/not-expired? (:token_exp user)) user)
    (handle-new-token token)))

(defn- send-invite-email! [email invited-by]
  (let [cred    (config/mailgun-cred)
        body    (format "Lucky day! \n %s invited you to try out a new way to manage the list of things you need to get done. Try DO.IT at %s" invited-by (:domain cred))
        content {:from    (str "no-reply@" (:domain cred))
                 :to      email
                 :subject "You are invited to try DO.IT"
                 :html    body}]
    (mail/send-mail (config/mailgun-cred) content)))

(defn invite-user! [email invited-by]
  (user-db/create-or-update! {:email email})
  (send-invite-email! email invited-by)
  email)
