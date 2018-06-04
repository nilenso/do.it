(ns doit.route
  (:require [doit.todo.route :as todo-route]
            [doit.auth.route :as auth-route]))

(def route ["/api/" {"todo/" todo-route/route
                     "auth/" auth-route/route}])
