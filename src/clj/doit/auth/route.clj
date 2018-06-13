(ns doit.auth.route
  (:require [doit.auth.handler :as handler]
            [doit.middleware :as mw]))

(def routes {"client-id/" {:get handler/client-id}
             "logout/"    {:post (mw/wrap-token handler/logout)}})
