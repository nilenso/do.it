(ns doit.db
  [:require
   [ragtime.jdbc :as jdbc]
   [ragtime.repl :as repl]
   [doit.config :as config]])

(defn load-config []
  {:datastore (jdbc/sql-database config/db)
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
