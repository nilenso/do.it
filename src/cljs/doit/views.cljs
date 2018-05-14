(ns doit.views
  (:require [re-frame.core :as re-frame]
            [doit.subs :as subs]
            ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div "Hello from " @name]))
