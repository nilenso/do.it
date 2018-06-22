(ns doit.todo-list.handler
  (:require
   [doit.todo-list.spec :as spec]
   [doit.todo-list.db :as todo-list-db]
   [clojure.spec.alpha :as s]))

(defn- create* [params]
  {:status 201
   :body   (-> params
               todo-list-db/create!
               (select-keys [:name :id]))})

(defn create! [request]
  (let [body        (:body request)
        parsed-body (s/conform ::spec/create-params body)]
    (if (= parsed-body ::s/invalid)
      {:status 400
       :body   {:error (s/explain-str ::spec/create-params body)}}
      (create* parsed-body))))


(defn list-all [request]
  {:status 200
   :body   (todo-list-db/list-all)})
