(ns doit.todo-list.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn create!
  [values]
  (first
   (-> (jdbc/insert! (config/db) :todo_list values))))

(defn list-all
  []
  (-> (jdbc/query (config/db) ["SELECT * FROM todo_list"])))
