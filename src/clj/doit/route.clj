(ns doit.route
  (:require [doit.todo.route :as todo-route]
            [doit.auth.route :as auth-route]
            [doit.middleware :as mw]))

(def routes ["/api/" {"todo/" todo-route/routes
                      "auth/" auth-route/routes}])
