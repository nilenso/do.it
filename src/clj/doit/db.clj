(ns doit.db
  [:require
   [ragtime.jdbc :as jdbc]
   [ragtime.repl :as repl]])

;; TODO: specify the connection-uri in profiles.clj
(defn load-config []
  {:datastore (jdbc/sql-database {:dbname "doit" :dbtype "postgresql"})
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
