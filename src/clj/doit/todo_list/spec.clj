(ns doit.todo-list.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::id int?)

(s/def ::name string?)

(s/def ::create-params (s/keys :req-un [::name]))

(s/def ::todo-list (s/keys :req-un [::id ::name]))
