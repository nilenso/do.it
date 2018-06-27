(ns doit.route
  (:require [doit.todo.route :as todo-route]
            [doit.auth.route :as auth-route]
            [doit.todo-list.route :as todo-list-route]
            [doit.middleware :as mw]))

(def routes ["/" {"api/" {"todo/"      todo-route/routes
                          "auth/"      auth-route/routes
                          "todo-list/" todo-list-route/routes}
                  true   (fn [request] {:status 404 :body {:error "Not Found"}})}])
