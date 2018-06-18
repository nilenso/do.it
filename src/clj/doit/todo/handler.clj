(ns doit.todo.handler
  (:require
   [clojure.data.json :as json]
   [doit.todo.spec :as spec]
   [doit.todo.db :as todo-db]
   [clojure.spec.alpha :as s]))

(defn wrap-response [data status]
  {:status status
   :body data})

(defn create-todo* [params]
  (-> params
      todo-db/add!
      (select-keys [:content :id :done])
      (wrap-response 201)))

(defn create-todo [request]
  (let [body   (:body request)
        parsed-body (s/conform ::spec/create-params body)]
    (if (= parsed-body ::s/invalid)
      (wrap-response {:error (s/explain-str ::spec/create-params body)} 400)
      (create-todo* parsed-body))))

(defn update-todo* [updated-todo]
  (-> updated-todo
      todo-db/update!
      (select-keys [:content :id :done])
      (wrap-response 200)))

(defn update-todo [request]
  (let [body (:body request)
        id   (Integer. (get-in request [:route-params :id]))
        todo (todo-db/retrieve id)]
    (if-not todo
      (wrap-response {:error (format "todo with id %s not found" id)} 404)
      (let [parsed-body (s/conform ::spec/update-params body)]
        (if (= parsed-body ::s/invalid)
          (wrap-response {:error (s/explain-str ::spec/update-params body)} 400)
          (let [updated-todo (select-keys (merge todo parsed-body) [:content :id :done])]
            (update-todo* updated-todo)))))))

(defn list-todos [request]
  (let [todos (todo-db/list-all)]
    (wrap-response
     (map #(select-keys % [:content :id :done]) todos)
     200)))

(defn delete-todo [request]
  (let [id (Integer. (get-in request [:route-params :id]))]
    (todo-db/delete! id)
    {:status 204}))
