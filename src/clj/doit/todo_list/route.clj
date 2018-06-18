(ns doit.todo-list.route
  (:require [doit.todo-list.handler :as handler]
            [doit.middleware :as mw]))

(def routes {"" {:get  (mw/wrap-token handler/list-all)
                 :post (mw/wrap-token handler/create!)}})
