{:dev-environ
 {:env
  {:port "4000"
   :db-connection-string "jdbc:postgresql://localhost:5432/doit"
   :cp-max-idle-time-excess-connections "1800"
   :cp-max-idle-time "10800"
   :log-file-prefix "logs/doit.log"
   :app-log-level "debug"
   :allowed-hosted-domain "*"}}

 :test-environ
 {:env
  {:port "4001"
   :db-connection-string "jdbc:postgresql://localhost:5432/doit_test"
   :allowed-hosted-domain "*"}}}
