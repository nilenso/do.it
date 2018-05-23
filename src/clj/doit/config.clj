(ns doit.config
  [:require
   [aero.core :as aero]
   [clojure.java.io :as io]])

(def ^:private config (atom nil))

(defn load []
  (let [profile (keyword (System/getenv "PROFILE"))]
    (reset! config (aero/read-config (io/resource "config.edn")
                                     {:profile profile}))))
(defn db []
  (get @config :db))

(defn webserver []
  (get @config :webserver))

(defn api-end-point []
  (let [{:keys [host port]} (webserver)]
    (format "http://%s:%s/api/" host port)))
