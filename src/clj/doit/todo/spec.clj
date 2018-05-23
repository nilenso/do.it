(ns doit.todo.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [doit.todo.api :as api]))

(s/def ::content string?)

(s/def ::id int?)

(s/def ::done boolean?)

(s/def ::todo-in (s/keys :req-un [::content]))

(s/def ::todo-out (s/keys :req-un [::content ::id ::done]))

(s/def ::todo-list (s/coll-of ::todo-out))

(s/def ::mark-done-in (s/keys :req-un [::id]))

(s/def ::mark-undone-in (s/keys :req-un [::id]))
