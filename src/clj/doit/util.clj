(ns doit.util
  (:require [environ.core :as environ]))

(defn from-config
  [config-var]
  (if-let [result (environ/env config-var)]
    result
    (throw (ex-info "Config var not defined" {:var config-var}))))

(def connection (from-config :db-connection-string))
