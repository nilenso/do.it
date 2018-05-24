(ns doit.todo.handler
  (:require
   [clojure.data.json :as json]
   [doit.todo.spec :as spec]
   [doit.todo.db :as todo-db]
   [clojure.spec.alpha :as s]))

(defn parse-body [req-body]
  (-> req-body
      (.bytes)
      (slurp)
      (json/read-str :key-fn keyword)))

(defn wrap-response [json-body status]
  {:status status
   :headers {"Content-Type" "application/json"}
   :body (json/write-str json-body)})

(defn create-todo [request]
  (let [body   (parse-body (:body request))
        parsed-body (s/conform ::spec/todo-in body)]
    (if (= parsed-body ::s/invalid)
      (wrap-response {:error (s/explain-str ::spec/todo-in body)} 400)
      (-> parsed-body
          todo-db/add-todo!
          (select-keys [:content :id :done])
          (wrap-response 201)))))

(defn list-todos [request]
  (let [todos (todo-db/list-todos)]
    (wrap-response
     (map #(select-keys % [:content :id :done]) todos)
     200)))

(defn mark-done [request]
  (let [id (get-in request [:route-params :id])
        res (-> id (Integer.) todo-db/mark-done! first)]
    (if res
      (wrap-response (select-keys res [:content :id :done]) 200)
      (wrap-response {:error (format "todo with id %s not found" id)} 404))))

(defn mark-undone [request]
  (let [id (get-in request [:route-params :id])
        res (-> id (Integer.) todo-db/mark-undone! first)]
    (if res
      (wrap-response (select-keys res [:content :id :done]) 200)
      (wrap-response {:error (format "todo with id %s not found" id)} 404))))
