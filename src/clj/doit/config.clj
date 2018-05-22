(ns doit.config
  [:require
   [aero.core :refer [read-config]]])

(def ^:private profile (keyword (System/getenv "PROFILE")))
(def ^:private config (read-config (clojure.java.io/resource "config.edn")
                                   {:profile profile}))

(def db (get config :db))

(def webserver (get config :webserver))

(def rest-api-end-point (str "http://"
                        (:host webserver)
                        ":"
                        (:port webserver)
                        "/api/"))

(def ws-end-point (str "ws://"
                        (:host webserver)
                        ":"
                        (:port webserver)
                        "/api/todo/ws"))
