(ns doit.todo.handler
  (:require
   [clojure.data.json :as json]
   [doit.todo.spec :as spec]
   [doit.todo.db :as todo-db]
   [clojure.spec.alpha :as s]))

(defn parse-body [req-body]
  (json/read-str (slurp (.bytes req-body)) :key-fn keyword))

(defn response-400 [err]
  {:status  400
   :headers {"Content-Type" "application/json"}
   :body    err})

(defn response-201 [body]
  {:status  201
   :headers {"Content-Type" "application/json"}
   :body    body})

(defn create-todo [request]
  (let [body   (parse-body (:body request))
        parsed-body (s/conform ::spec/todo-in body)]
    (if (= parsed-body ::s/invalid)
      (response-400 (s/explain-str ::spec/todo-in body))
      (response-201 (-> parsed-body
                        todo-db/add-todo!
                        first
                        (select-keys [:content :id])
                        json/write-str)))))

(defn list-todos [request]
  (let [todos (todo-db/list-todos)]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    (json/write-str (map #(select-keys % [:content :id]) todos))}))
