(ns doit.fixtures
  (:require [doit.core :refer [start-server! stop-server!]]
            [doit.db :refer [migrate]]
            [clojure.java.jdbc :as jdbc]
            [doit.config :as config]))

(defn load-config [f]
  (config/load)
  (f))

(defn start-stop-server [f]
  (start-server!)
  (f)
  (stop-server!))

(defn destroy-db []
  (jdbc/with-db-transaction [conn (config/db)]
    (jdbc/execute! conn "DROP SCHEMA IF EXISTS public CASCADE;")
    (jdbc/execute! conn "CREATE SCHEMA IF NOT EXISTS public;")))

(defn migrate-destroy-db [f]
  (migrate)
  (f)
  (destroy-db))

(defn isolate-db [f]
  (jdbc/with-db-transaction [conn (config/db)]
    (jdbc/db-set-rollback-only! conn)
    (with-redefs [config/db (fn [] conn)]
      (f))))
