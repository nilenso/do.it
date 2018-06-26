(ns doit.todo-list.route
  (:require [doit.todo-list.handler :as handler]
            [doit.middleware :as mw]))

(def routes {"" {:get      (mw/wrap-token handler/list-all)
                 :post     (mw/wrap-token handler/create!)
                 [:id "/"] {:put    (mw/wrap-token handler/update!)
                            :delete (mw/wrap-token handler/delete!)}}})
