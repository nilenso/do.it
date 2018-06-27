(ns doit.config
  [:require
   [aero.core :as aero]
   [clojure.java.io :as io]])

(def ^:private config (atom nil))

(defn load-config
  ([]
   (load-config (keyword (System/getenv "PROFILE"))))
  ([profile]
   (reset! config (aero/read-config (io/resource "config.edn")
                                    {:profile profile}))))
(defn db []
  (get @config :db))

(defn webserver []
  (get @config :webserver))

(defn google-client-id []
  (get @config :google-client-id))

(defn mailgun-cred []
  {:key    (get-in @config [:mailgun :key])
   :domain (get-in @config [:mailgun :domain])})
