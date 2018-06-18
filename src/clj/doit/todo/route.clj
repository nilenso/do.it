(ns doit.todo.route
  (:require [doit.todo.handler :as handler]
            [doit.middleware :as mw]))

(def routes {"" {:post     (mw/wrap-token handler/create!)
                 :get      (mw/wrap-token handler/list-all)
                 [:id "/"] {:put    (mw/wrap-token handler/update!)
                            :delete (mw/wrap-token handler/delete!)}}})
