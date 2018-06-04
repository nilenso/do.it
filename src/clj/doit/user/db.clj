(ns doit.user.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn create-user!
  [values]
  (first (jdbc/insert! (config/db) :app_user values)))

(defn update-user!
  [values]
  (jdbc/update! (config/db) :app_user values ["id = ?" (:id values)]))

(defn get-user-by-token
  [token]
  (first (jdbc/query
          (config/db)
          ["SELECT * FROM app_user WHERE token = ?" token])))
