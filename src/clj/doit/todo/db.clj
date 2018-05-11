(ns doit.todo.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.db :as db]
            [yesql.core :refer [defqueries]]))

(defqueries "doit/todo/sql/db.sql")

(defn create!
  [connection contents]
  (create-todo-query! contents {:connection connection}))

(defn retrieve-all
  [connection]
  (retrieve-all-todo-query {} {:connection connection}))
