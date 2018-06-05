(ns doit.auth.auth
  (:require
   [org.httpkit.client :as http]
   [doit.user.db :as user-db]))

(defn token-info-url [token]
  (format "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s" token))

(defn current-unix-time []
  (quot (System/currentTimeMillis) 1000))

(defn token-valid? [token]
  (if-let [user (user-db/get-user-by-token token)]
    (< (current-unix-time) (:token_exp user))
    false))
