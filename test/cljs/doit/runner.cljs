(ns doit.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [doit.core-test]))

(doo-tests 'doit.core-test)
