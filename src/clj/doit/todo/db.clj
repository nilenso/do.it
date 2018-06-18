(ns doit.todo.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn add!
  [values]
  (-> (jdbc/insert! (config/db) :todo values)
      first))

(defn update!
  [todo]
  (jdbc/update! (config/db) :todo todo ["id = ?" (:id todo)])
  todo)

(defn retrieve
  [id]
  (first (jdbc/query (config/db) ["SELECT * FROM todo WHERE id = ?" id])))

(defn list-all
  []
  (jdbc/query (config/db) ["SELECT * FROM todo"]))

(defn delete!
  [id]
  (jdbc/delete! (config/db) :todo ["id = ?" id]))
