(ns doit.route
  (:require [doit.todo.route :as todo-route]))

(def route ["/api/" {"todo/" todo-route/route}])
