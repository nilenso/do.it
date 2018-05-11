(ns doit.web.routes
  (:require
   [doit.todo.routes :as todo]
   [doit.web.util :as web-util]))

(defn routes []
  ["/" [["api/"      {"todo/"      (todo/routes)}]
        [true   (fn [_] web-util/error-not-found)]]])
