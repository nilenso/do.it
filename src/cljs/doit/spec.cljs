(ns doit.spec
  (:require [clojure.spec.alpha :as s]))

;; -- TODO --
(s/def ::id int?)

(s/def ::listid int?)

(s/def ::content string?)

(s/def ::done boolean?)

(s/def ::todo (s/keys :req-un [::id ::content ::done ::listid]))

(s/def ::todos (s/map-of ::id ::todo))

;; -- TODO LISTS --
(s/def ::name string?)

(s/def ::archived boolean?)

(s/def ::todo-list (s/keys :req-un [::id ::name ::archived]))

(s/def ::todo-lists (s/map-of ::id ::todo-list))

;; -- MISC --
(s/def ::client-id string?)

(s/def ::token string?)

;; -- APP DB --
(s/def ::app-db (s/keys :req-un [::todos ::todo-lists] :opt-un [::client-id ::token]))
