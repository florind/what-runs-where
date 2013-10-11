(ns what-runs-where.handler
  (:use what-runs-where.models 
        what-runs-where.views
        compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as server]
            [clojure.tools.cli :as c])
  (:gen-class))

(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

(defn load-config
		"loads the config file in a var called server-map. This var is used further by the routing logic, rebound via -main or via tests (where a test config file can be loaded)
		The config file format is:
			Cluster Name={:servers [], :parsers [{:urs \"the-service-url\" :regexp #\"version-regexp-parser\", :name \"service-name\"},...]}
			See the supplied example, there are some formatting gotchas inherited from how Java handles properties files"
  [config-file]
  (let [cfg (java.util.Properties.)
        file (java.io.FileInputStream. config-file)]
    (.load cfg file)
    (def server-map (into {} (for [[k v] cfg] [k (read-string v)])))))

(defroutes app-routes
	;the last argument to overview-page is the version-aggregator function which will be called within the scope of the said function.
	(GET "/" [] (overview-page server-map version-aggregator))
  (GET "/api/services" []
       (let [comb-map (map #(version-aggregator (val %)) server-map)]
         (cheshire.core/generate-string comb-map)))
  (GET "/help" [] (help-page))
  (GET "/version" [] (str "{\"version\": \"0.1.0\",\n \"clojure\": \"" (clojure-version) "\"}"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main [& args]
  (let [[parsed-args trailing-args doc] (c/cli args 
       ["-c" "Configuration file" :default "config.properties"]
       ["-p" "Port" :default 8000 :parse-fn #(Integer. %)])]
    (load-config (:c parsed-args))
    (println doc)
  (server/run-jetty (var app) {:port (:p parsed-args) :join? false})))
