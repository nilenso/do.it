(ns doit.ws
  (:require
   [wscljs.client :as ws-client]
   [clojure.data.json :as json]))


(def socket (atom nil))

(def handlers {:on-message (fn [e] (ws-recieve-handler (json/read-str (.-data e))))
               :on-open #(prn "Opening a socket new connection")
               :on-close #(prn "Closing a connection")})

(defn create-ws-connection []
  (reset! socket (ws-client/create "ws://localhost:4000/api/todo/ws/" handlers)))

(defn ws-recieve-handler [msg]
  (prn msg))
