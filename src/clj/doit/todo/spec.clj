(ns doit.todo.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [doit.todo.api :as api]))

(s/def ::content string?)

(s/def ::id int?)

(s/def ::todo-in (s/keys :req-un [::content]))

(s/def ::todo-out (s/keys :req-un [::content ::id]))

(s/def ::todo-list (s/coll-of ::todo-out))
