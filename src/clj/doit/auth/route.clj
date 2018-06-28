(ns doit.auth.route
  (:require [doit.auth.handler :as handler]
            [doit.middleware :as mw]))

(def routes {"client-id/"    {:get handler/client-id}
             "logout/"       {:post (mw/wrap-token handler/logout)}
             "invite-user/"  {:post (mw/wrap-token handler/invite!)}
             "verify-token/" {:post handler/verify-token}})
