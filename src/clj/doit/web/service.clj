(ns doit.web.service
  (:require [bidi.ring :refer [make-handler]]
            [ring.middleware.json :refer [wrap-json-response
                                          wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer :all]
            [doit.web.routes :refer [routes]]
            [ring.middleware.cors :refer [wrap-cors]]
            [doit.web.middleware :refer [wrap-validate
                                         wrap-log-request-response
                                         wrap-error-logging]]))

(defn handler []
  (make-handler (routes)))

(defn app []
  (-> (handler)
      (wrap-validate)
      (wrap-log-request-response)
      (wrap-error-logging)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-params)
      (wrap-cors
       :access-control-allow-origin [#"http://localhost:3449"]
       :access-control-allow-methods [:get :put :post :delete])
      (wrap-defaults api-defaults)))
