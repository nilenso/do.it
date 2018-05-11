(ns doit.web.util
  (:require [ring.util.response :as res]
            [doit.util :as util]
            [clojure.algo.generic.functor :refer [fmap]]))

(defn error-response
  [status msg]
  (-> (res/response {:error msg})
      (res/status status)))

(def error-forbidden
  (error-response 403 "Forbidden"))

(def error-method-not-allowed
  (error-response 405 "Method not allowed"))

(def error-not-found
  (error-response 404 "Not found"))

(def error-bad-request
  (error-response 400 "Bad request"))

(def error-internal-server-error
  (error-response 500 "Internal Server Error"))
