(ns doit-web.views
  (:require [re-frame.core :as re-frame]
            [doit-web.subs :as subs]
            ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div "Hello from " @name]))
