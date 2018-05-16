(ns doit.fixtures
  (:require [doit.core :refer [start-server! stop-server!]]))

(defn start-stop-server [f]
  (start-server!)
  (f)
  (stop-server!))
