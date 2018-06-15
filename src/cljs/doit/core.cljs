(ns doit.core
  (:refer-clojure :exclude [subs])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [doit.events :as events]
            [doit.subs :as subs]
            [doit.auth :as auth]
            [doit.views :as views]
            [doit.config :as config]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (enable-console-print!)
  (events/init)
  (subs/init)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/get-client-id])
  (auth/init)
  (mount-root))
