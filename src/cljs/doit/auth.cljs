(ns doit.auth
  (:refer-clojure :exclude [subs])
  (:require [doit.config :as config]
            [doit.subs :as subs]
            [doit.events :as events]
            [re-frame.core :as rf]
            [goog.object]))

(defn get-instance []
  (.getAuthInstance (goog.object/get js/gapi "auth2")))

(defn sign-in-success [gapi-user]
  (let [token (.-id_token (.getAuthResponse gapi-user))]
    (rf/dispatch [::events/save-auth-token token])
    (prn "sign in success")))

(defn sign-in-failure []
  (prn "sign in failed"))

(defn sign-in []
  (-> (.signIn (get-instance))
      (.then sign-in-success sign-in-failure)))

(defn sign-out []
  (rf/dispatch [::events/sign-out]))

(defn on-gapi-load []
  (prn "gapi auth2 loaded")
  (.init (goog.object/get js/gapi "auth2") (clj->js {:client_id @(rf/subscribe [::subs/client-id])})))

(defn load-gapi []
  (.load js/gapi "auth2" on-gapi-load))

(defn init []
  (load-gapi))
