(ns doit.todo.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn add-todo!
  [values]
  (-> (jdbc/insert! (config/db) :todo values)
      first))

(defn update-todo!
  [todo]
  (jdbc/update! (config/db) :todo todo ["id = ?" (:id todo)])
  todo)

(defn retrieve-todo
  [id]
  (first (jdbc/query (config/db) ["SELECT * FROM todo WHERE id = ?" id])))

(defn list-todos
  []
  (jdbc/query (config/db) ["SELECT * FROM todo"]))
