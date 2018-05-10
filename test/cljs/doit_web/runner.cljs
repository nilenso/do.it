(ns doit-web.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [doit-web.core-test]))

(doo-tests 'doit-web.core-test)
