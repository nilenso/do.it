(ns doit.config
  [:require
   [aero.core :refer [read-config]]])

(def ^:private config (read-config (clojure.java.io/resource "config.edn")))

(def db (get config :db))
