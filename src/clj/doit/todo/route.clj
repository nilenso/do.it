(ns doit.todo.route
  (:require [doit.todo.handler :as handler]))

(def route {"" {:post handler/create-todo
                :get handler/list-todos}})
