(ns doit-web.config)

;; TODO: Use enviornment to configure dev and prod urls

(def srv-url "http://localhost:4000/")

(def debug?
  ^boolean goog.DEBUG)
