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

(defn wrap-response [data status]
  {:status status
   :headers {"Content-Type" "application/json"}
   :body (json/write-str data)})

(defn create-todo [request]
  (let [body   (parse-body (:body request))
        parsed-body (s/conform ::spec/create-params body)]
    (if (= parsed-body ::s/invalid)
      (wrap-response {:error (s/explain-str ::spec/create-params body)} 400)
      (-> parsed-body
          todo-db/add-todo!
          (select-keys [:content :id :done])
          (wrap-response 201)))))

(defn update-todo [request]
  (let [body (parse-body (:body request))
        id (Integer. (get-in request [:route-params :id]))
        todo (todo-db/retrieve-todo id)]
    (if-not todo
      (wrap-response {:error (format "todo with id %s not found" id)} 404)
      (let [parsed-body (s/conform ::spec/update-params body)]
        (if (= parsed-body ::s/invalid)
                (wrap-response {:error (s/explain-str ::spec/update-params body)} 400)
                (let [updated-todo (select-keys (merge todo parsed-body) [:content :id :done])]
                  (-> updated-todo
                      todo-db/update-todo!
                      (select-keys [:content :id :done])
                      (wrap-response 200))))))))

(defn list-todos [request]
  (let [todos (todo-db/list-todos)]
    (wrap-response
     (map #(select-keys % [:content :id :done]) todos)
     200)))
