(ns doit.todo.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::content string?)

(s/def ::id int?)

(s/def ::list_id int?)

(s/def ::done boolean?)

(s/def ::create-params (s/keys :req-un [::content ::list_id]))

(s/def ::update-params (s/keys :req-un [::content ::done ::id ::list_id]))

(s/def ::todo-out (s/keys :req-un [::content ::id ::done ::list_id]))

(s/def ::mark-done-in (s/keys :req-un [::id]))

(s/def ::mark-undone-in (s/keys :req-un [::id]))
