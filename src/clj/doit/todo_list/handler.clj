(ns doit.todo-list.handler
  (:require
   [doit.todo-list.spec :as spec]
   [doit.todo-list.db :as todo-list-db]
   [doit.util :as util]
   [clojure.spec.alpha :as s]))


(defn- create* [params]
  (util/wrap-response
   (-> params
       todo-list-db/create!
       (select-keys [:name :id]))
   201))

(defn create! [request]
  (let [body        (:body request)
        parsed-body (s/conform ::spec/create-params body)]
    (if (= parsed-body ::s/invalid)
      {:status 400
       :body   {:error (s/explain-str ::spec/create-params body)}}
      (create* parsed-body))))

(defn list-all [request]
  (util/wrap-response (todo-list-db/list-all) 200))

(defn- update* [updated]
  (-> updated
      todo-list-db/update!
      (select-keys [:name :id])
      (util/wrap-response 200)))

(defn update! [request]
  (let [body      (:body request)
        id        (Integer. (get-in request [:route-params :id]))
        todo-list (todo-list-db/retrieve id)]
    (if-not todo-list
      (util/wrap-response {:error (format "todo-list with id %s not found" id)} 404)
      (let [parsed-body (s/conform ::spec/update-params body)]
        (if (= parsed-body ::s/invalid)
          (util/wrap-response {:error (s/explain-str ::spec/update-params body)} 400)
          (update* (merge todo-list parsed-body)))))))

(defn delete! [request]
  (let [id (Integer. (get-in request [:route-params :id]))]
    (todo-list-db/delete! id)
    {:status 204}))
