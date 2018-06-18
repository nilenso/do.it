(ns doit.user.db
  (:require [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn create!
  [values]
  (first (jdbc/insert! (config/db) :app_user values)))

(defn update!
  [user]
  (jdbc/update! (config/db) :app_user user ["id = ?" (:id user)] {:return-keys true})
  ;; XXX: This will fail silently if there is something wrong with the map user
  user)

(defn get-by-token
  [token]
  (first (jdbc/query
          (config/db)
          ["SELECT * FROM app_user WHERE token = ?" token])))

(defn get-by-email
  [email]
  (first (jdbc/query
          (config/db)
          ["SELECT * FROM app_user WHERE email = ?" email])))

(defn create-or-update!
  [values]
  (if-let [{:keys [id]} (get-by-email (:email values))]
    (update! (assoc values :id id))
    (create! values)))
