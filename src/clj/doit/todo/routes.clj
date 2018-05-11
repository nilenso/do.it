(ns doit.todo.routes
  (:require [doit.todo.handlers :as handlers]))

(defn routes []
  {"" {:post handlers/create
       :get  handlers/retrieve-all}})
