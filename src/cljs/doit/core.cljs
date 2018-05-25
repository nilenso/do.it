(ns doit.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [doit.events :as events]
            [doit.views :as views]
            [doit.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/get-todos])
  (dev-setup)
  (mount-root))
