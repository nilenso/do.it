(ns doit.todo-list.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn create!
  [values]
  (first
   (jdbc/insert! (config/db) :todo_list values)))

(defn list-all
  []
  (jdbc/query (config/db) ["SELECT * FROM todo_list"]))

(defn retrieve
  [id]
  (first (jdbc/query (config/db) ["SELECT * FROM todo_list WHERE id = ?" id])))

(defn update!
  [todo-list]
  (jdbc/update! (config/db) :todo_list todo-list ["id = ?" (:id todo-list)])
  todo-list)

(defn delete!
  [id]
  (jdbc/delete! (config/db) :todo_list ["id = ?" id]))
