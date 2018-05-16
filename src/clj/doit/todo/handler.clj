(ns doit.todo.handler
  (:require
   [clojure.data.json :as json]
   [doit.todo.spec :as spec]
   [clojure.spec.alpha :as s]))

(defn parse-body  [req-body]
  (clojure.walk/keywordize-keys (json/read-str (slurp (.bytes req-body)))))

(defn create-todo [request]
  (let [body   (parse-body (:body request))
        parsed (s/conform ::spec/todo-in body)]
    (if (= parsed ::s/invalid)
      {:status  400
       :headers {"Content-Type" "application/json"}
       :body    (s/explain-str ::spec/todo-in body)}
      {:status  201
       :headers {"Content-Type" "application/json"}
       :body    "trying to create todo but can't"})))

(defn list-todos [request]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    "trying to list todos but can't"})
