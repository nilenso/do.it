(ns doit.todo.route
  (:require [doit.todo.handler :as handler]
            [doit.todo.ws :as todo-ws]))

(def route {"ws/" todo-ws/handler
            "" {:post handler/create-todo
                :get handler/list-todos}})
