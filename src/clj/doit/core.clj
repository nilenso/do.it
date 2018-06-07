(ns doit.core
  (:gen-class)
  (:require [org.httpkit.server :as httpkit]
            [doit.config :as config]
            [doit.routes :refer [routes]]
            [doit.middleware :as mw]
            [doit.db :as db]
            [bidi.ring :refer [make-handler]]))

(defonce server (atom nil))

(def handler (->
              routes
              make-handler
              mw/wrap-json-request
              mw/wrap-json-response))

(defn start-server! []
  (let [port (:port (config/webserver))]
    (reset! server (httpkit/run-server handler {:port port}))
    (println "Server started at port: " port)))

(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server! []
  (stop-server!)
  (start-server!))

(defn -main [& args]
  (config/load-config)
  (db/migrate)
  (start-server!))
