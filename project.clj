(defproject immutant-ws "1.0.0-SNAPSHOT"
  :description "Simple wildfly server"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.immutant/web "2.0.0"]
                 [org.immutant/messaging "2.0.0"]
                 ;;[org.immutant/caching "2.0.0"]
                 ;;[org.immutant/messaging "2.0.0"]
                 ;;[org.immutant/scheduling "2.0.0"]                 
                 [ring/ring-core "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [compojure "1.3.1"]
                 [cheshire "5.3.1"]
                 [environ "1.0.0"]
                 ]
  :min-lein-version "2.5.0"
  :main immutant-ws.server
  :plugins [[lein-immutant "2.0.0"]]
  :uberjar-name "immutant-ws-standalone.jar"
  :profiles {:uberjar {:aot [immutant-ws.server]}})

