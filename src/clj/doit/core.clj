(ns doit.core
  (:require [org.httpkit.server :as httpkit]
            [doit.config :as config]
            [doit.route :refer [route]]
            [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler]]))

(defonce server (atom nil))

(def handler (make-handler route))

(defn start-server! []
  (reset! server (httpkit/run-server handler {:port (:port config/webserver)})))

(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn restart-server! []
  (stop-server!)
  (start-server!))
