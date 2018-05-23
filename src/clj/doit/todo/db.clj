(ns doit.todo.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn add-todo!
  [values]
  (jdbc/insert! (config/db) :todo values))

(defn mark-done!
  [id]
  (jdbc/query (config/db) ["UPDATE todo SET done='true' WHERE id = ? RETURNING *" id]))

(defn mark-undone!
  [id]
  (jdbc/query (config/db) ["UPDATE todo SET done='false' WHERE id = ? RETURNING *" id]))

(defn retrieve-todo
  [id]
  (first (jdbc/query (config/db) ["SELECT * FROM todo WHERE id = ?" id])))

(defn list-todos
  []
  (jdbc/query (config/db) ["SELECT * FROM todo"]))
