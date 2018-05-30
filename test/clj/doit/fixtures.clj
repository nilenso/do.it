(ns doit.fixtures
  (:require [doit.core :refer [start-server! stop-server!]]
            [doit.db :refer [migrate]]
            [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn load-config [f]
  (config/load-config :test)
  (f))

(defn start-stop-server [f]
  (start-server!)
  (f)
  (stop-server!))

(defn conn-uri []
  (let [{:keys [host port dbname dbtype user password]} (config/db)]
    {:connection-uri
     (format "jdbc:%s://%s:%s/?user=%s&password=%s"
             dbtype
             host
             port
             user
             password)}))

(defn create-db []
  (let [{:keys [dbname]} (config/db)
        cmd              (format "CREATE DATABASE %s" dbname)]
    (jdbc/db-do-commands (conn-uri) false cmd)))

(defn destroy-db []
  (let [{:keys [dbname]} (config/db)
        cmd              (format "DROP DATABASE %s" dbname)]
    (jdbc/db-do-commands (conn-uri) false cmd)))

(defn create-migrate-destroy-db [f]
  (create-db)
  (migrate)
  (f)
  (destroy-db))

(defn isolate-db [f]
  (jdbc/with-db-transaction [conn (config/db)]
    (jdbc/db-set-rollback-only! conn)
    (with-redefs [config/db (fn [] conn)]
      (f))))
