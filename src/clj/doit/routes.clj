(ns doit.routes
  (:require [doit.todo.routes :as todo-routes]
            [doit.auth.routes :as auth-routes]
            [doit.middleware :as mw]))

(def routes ["/api/" {"todo/" todo-routes/routes
                     "auth/" auth-routes/routes}])
