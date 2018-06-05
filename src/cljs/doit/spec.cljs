(ns doit.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::id int?)

(s/def ::content string?)

(s/def ::done boolean?)

(s/def ::todo (s/keys :req-un [::id ::content ::done]))

(s/def ::todos (s/map-of ::id ::todo))

(s/def ::client-id string?)

(s/def ::token string?)

(s/def ::app-db (s/keys :req-un [::todos] :opt-un [::client-id ::token]))
