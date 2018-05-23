(ns doit.todo.route
  (:require [doit.todo.handler :as handler]))

(def route {"" {:post handler/create-todo
                :get handler/list-todos}
            [:id "/mark_done/"] {:post handler/mark-done}
            [:id "/mark_undone/"] {:post handler/mark-undone}})
