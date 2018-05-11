(ns doit-web.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [doit-web.events :as events]
            [doit-web.views :as views]
            [doit-web.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [:get-todos])
  (dev-setup)
  (mount-root))
