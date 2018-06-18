(ns doit.todo.handler
  (:require
   [clojure.data.json :as json]
   [doit.todo.spec :as spec]
   [doit.todo.db :as todo-db]
   [clojure.spec.alpha :as s]))

(defn wrap-response [data status]
  {:status status
   :body data})

(defn create* [params]
  (-> params
      todo-db/add!
      (select-keys [:content :id :done :list_id])
      (wrap-response 201)))

(defn create! [request]
  (let [body        (:body request)
        parsed-body (s/conform ::spec/create-params body)]
    (if (= parsed-body ::s/invalid)
      (wrap-response {:error (s/explain-str ::spec/create-params body)} 400)
      (create* parsed-body))))

(defn update* [updated]
  (-> updated
      todo-db/update!
      (select-keys [:content :id :done :list_id])
      (wrap-response 200)))

(defn update! [request]
  (let [body (:body request)
        id   (Integer. (get-in request [:route-params :id]))
        todo (todo-db/retrieve id)]
    (if-not todo
      (wrap-response {:error (format "todo with id %s not found" id)} 404)
      (let [parsed-body (s/conform ::spec/update-params body)]
        (if (= parsed-body ::s/invalid)
          (wrap-response {:error (s/explain-str ::spec/update-params body)} 400)
          (let [updated-todo (select-keys (merge todo parsed-body) [:content :id :done :list_id])]
            (update* updated-todo)))))))

(defn list-all [request]
  (let [todos (todo-db/list-all)]
    (wrap-response
     (map #(select-keys % [:content :id :done :list_id]) todos)
     200)))

(defn delete! [request]
  (let [id (Integer. (get-in request [:route-params :id]))]
    (todo-db/delete! id)
    {:status 204}))
