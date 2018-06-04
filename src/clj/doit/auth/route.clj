(ns doit.auth.route
  (:require [doit.auth.handler :as handler]))

(def route {"client-id" {:get handler/client-id}})
