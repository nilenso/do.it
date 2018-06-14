(ns doit.todo.route
  (:require [doit.todo.handler :as handler]
            [doit.middleware :as mw]))

(def routes {"" {:post     (mw/wrap-token handler/create-todo)
                 :get      (mw/wrap-token handler/list-todos)
                 [:id "/"] {:put    (mw/wrap-token handler/update-todo)
                            :delete (mw/wrap-token handler/delete-todo)}}})
