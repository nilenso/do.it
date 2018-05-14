(ns doit.fixtures
  (:require
   [clojure.java.jdbc :as jdbc]
   [doit.core :as core]
   [doit.db :as db]
   [doit.migration :refer [migrate-db]]
   [doit.web.service :refer [app]]
   [doit.util :as util]
   [org.httpkit.server :refer [run-server]]))

(defn init! [f]
  (core/init!)
  (f)
  (core/teardown!))

(defn destroy-db []
  (jdbc/with-db-transaction [conn (db/connection)]
    (jdbc/execute! conn "DROP SCHEMA IF EXISTS public CASCADE;")
    (jdbc/execute! conn "CREATE SCHEMA IF NOT EXISTS public;")))

(defn migrate-test-db [f]
  (migrate-db)
  (f)
  (destroy-db))

(defn serve-app [f]
  (let [stop-fn (run-server (app) {:port (Integer/parseInt (util/from-config :port))})]
    (f)
    (stop-fn :timeout 100)))

(defn isolate-db [f]
  (jdbc/with-db-transaction [conn (db/connection)]
    (jdbc/db-set-rollback-only! conn)
    (with-redefs [db/connection (fn [] conn)]
      (f))))
