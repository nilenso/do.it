(ns doit.todo.ws
  (:require [org.httpkit.server :as httpkit]
            [clojure.data.json :as json]
            [doit.todo.spec :as spec]
            [doit.todo.db :as todo-db]
            [clojure.spec.alpha :as s]))

(defonce clients (atom {}))

(defn send-all [msg]
  (doseq [client (keys @clients)]
    (httpkit/send! client (json/json-str msg))))

(defn add-todo-handler [channel data]
  (if (s/valid? ::spec/todo-in data)
    (let [added-todo (-> data
                         todo-db/add-todo!
                         first
                         (select-keys [:content :id]))]
      (send-all {:type "add"
                 :data added-todo}))
    (httpkit/send! channel
                   (json/json-str
                    {:type "error"
                     :data {:msg (s/explain-str ::spec/todo-in data)}}))))


(defn process-cmd [channel {:keys [command data]}]
  (condp = command
    "add" (add-todo-handler channel data)
    (httpkit/send! channel
                   (json/json-str
                    {:type "error"
                     :data {:msg (str "command not found: " command)}}))))

(defn process-msg [channel msg]
  (prn "recieved from websocket" msg)
  (let [parsed-msg (json/read-str msg :key-fn keyword)]
    (if (s/valid? ::spec/ws-data parsed-msg)
      (process-cmd channel parsed-msg)
      (httpkit/send! channel
                     (json/json-str
                      {:type "error"
                       :data {:msg (s/explain-str ::spec/ws-data parsed-msg)}})))))

(defn handler [request]
  (httpkit/with-channel request channel
    (swap! clients assoc channel true)
    (httpkit/on-close channel (fn [status]
                                (swap! clients dissoc channel)
                                (prn channel "closed, status" status)))
    (httpkit/on-receive channel (fn [data]
                                  (process-msg channel data)))))
