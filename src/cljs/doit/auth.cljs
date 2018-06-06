(ns doit.auth
  (:refer-clojure :exclude [subs])
  (:require [cljs.core.async :refer [put! >! <! take chan buffer ]]
            [doit.config :as config]
            [doit.subs :as subs]
            [doit.events :as events]
            [re-frame.core :as rf]
            [goog.object]))

(defn save-auth-token
  [cofx [_ token]]
  {:db (assoc-in (:db cofx) [:user :token] token)
   :dispatch [::events/get-todos]})

(rf/reg-event-fx
 ::save-auth-token
 save-auth-token)

(defn sign-in-success [gapi-user]
  (let [token (.-id_token (.getAuthResponse gapi-user))]
    (rf/dispatch [::save-auth-token token])
    (prn "sign in success")))

(defn sign-in-failure []
  (prn "sign in failed"))

(defn render-sign-btn []
  (let [opts {:onsuccess sign-in-success
              :onfailure sign-in-failure}]
    (.render (goog.object/get js/gapi "signin2") config/sign-in-btn-id (clj->js opts))))

(defn on-gapi-load []
  (prn "gapi auth2 loaded")
  (.init (goog.object/get js/gapi "auth2") (clj->js {:client_id @(rf/subscribe [::subs/client-id])}))
  (render-sign-btn))

(defn init []
  (.load js/gapi "auth2" on-gapi-load))
