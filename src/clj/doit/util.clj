(ns doit.util)

(defn current-unix-time []
  (quot (System/currentTimeMillis) 1000))

(defn not-expired? [exp]
  (< (current-unix-time) exp))

(defn wrap-response [data status]
  {:status status
   :body   data})
