(ns doit.auth
  (:require [cljs.core.async :refer [put! >! <! take chan buffer ]]
            [doit.config :as config]
            [re-frame.core :as rf]
            [goog.object]))

(defn save-auth-token
  [db [_ token]]
  (assoc-in db [:user :token] token))

(rf/reg-event-db
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
  (.init (goog.object/get js/gapi "auth2") (clj->js {:client_id (config/client-id)}))
  (render-sign-btn))

(defn init []
  (.load js/gapi "auth2" on-gapi-load))
