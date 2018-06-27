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

(defn handle-new-token [token]
  "Validates a google OAuth token and updates the token user if the user already exists in the db
   Reference: https://developers.google.com/identity/sign-in/web/backend-auth#verify-the-integrity-of-the-id-token"
  (let [{:keys [status body]} @(http/get (token-info-url token))
        parsed-body           (json/read-str body :key-fn keyword)]
    (when (and
           (= status 200)
           (util/not-expired? (Integer. (:exp parsed-body)))
           (= (:aud parsed-body) (config/google-client-id)))
      (when-let [user (user-db/get-by-email (:email parsed-body))]
        (user-db/update! {:email     (:email parsed-body)
                          :token     token
                          :token_exp (Integer. (:exp parsed-body))})))))

(defn handle-token [token]
  "Validates the authorization token and returns the corresponding user if
   token is valid, returns `nil` otherwise. Creates a new user if one doesn't exist already."
  (if-let [user (user-db/get-by-token token)]
    (when (util/not-expired? (:token_exp user)) user)
    (handle-new-token token)))

(defn- add-invited-user-to-db! [email]
  ;; Maybe we should also store the person who invited someone
  (user-db/create-or-update! {:email email}))

(defn- send-invite-email! [email invited-by]
  (let [cred    (config/mailgun-cred)
        body    (format "Lucky day! \n %s invited you to try out a new way to manage the list of things you need to get done. Try DO.IT at %s" invited-by (:domain cred))
        content {:from    (str "no-reply@" (:domain cred))
                 :to      email
                 :subject "You are invited to try DO.IT"
                 :html    body}]
    (mail/send-mail (config/mailgun-cred) content)))

(defn invite-user! [email invited-by]
  (add-invited-user-to-db! email)
  (send-invite-email! email invited-by)
  email)
