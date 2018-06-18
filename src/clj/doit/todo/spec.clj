(ns doit.todo.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::content string?)

(s/def ::id int?)

(s/def ::listid int?)

(s/def ::done boolean?)

(s/def ::create-params (s/keys :req-un [::content ::listid]))

(s/def ::update-params (s/keys :req-un [::content ::done ::id ::listid]))

(s/def ::todo-out (s/keys :req-un [::content ::id ::done ::listid]))

(s/def ::mark-done-in (s/keys :req-un [::id]))

(s/def ::mark-undone-in (s/keys :req-un [::id]))
