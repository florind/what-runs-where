(defproject what-runs-where "0.1.0-SNAPSHOT"
  :description "Shows an aggregated view of any web service version. JSON API available"
  :url "http://github.com/florind/what-runs-where"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [clj-http "0.7.2"]
                 [clj-time "0.5.1"]
                 [hiccup "1.0.3"]
                 [org.clojure/tools.cli "0.2.2"]
                 ]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler what-runs-where.handler/app}
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]}}
  :main what-runs-where.handler)
