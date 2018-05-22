(ns doit.route
  (:require [bidi.bidi :refer [match-route]]
            [doit.todo.route :as todo-route]))

(def route ["/api/" {"todo/" todo-route/route}])
