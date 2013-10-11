(ns what-runs-where.models
  (:require [clj-http.client :as http]))

(defn service-ver
  "Applies the given regexp to the body of the url GET response (if 200 OK). This function returns a pair keyed by service-key 
		and having as value the result of the regular expression. If the HTTP response is other than 200 
		the value is set to :garble. The http connection timeout is 5s.
Example return {:backend \"1.1\"}"
  [url regex service-key]
  (try
    (let [response (http/get url {:conn-timeout 1000, :socket-timeout 1000, :throw-exceptions false})
          status (response :status),
          body (get response :body),
          req-time (get response :request-time),
        ver-str (re-find regex body)]
;    (println ver-str)
      (case status
        200 (if (nil? ver-str) {service-key "error: garble"} {service-key (last ver-str)})
        {service-key (str "error: server status " status)}))
    (catch Exception e {service-key (str "error: " e)})))

(defn services-ver-map
  "Maps fn (which is service-ver composed witin version-aggregator) to server-names. Returns a map keyed by server-names items and having as value a version pair as defined in service-var
		Example: {\"url1\" {:web: \"1.2\"}, ...}"
  [fn server-names]
  (let [version-list (pmap fn server-names)]
    (zipmap server-names version-list)))

(defn version-aggregator 
  "This function aggregates the frontend/backend version for each of the servers in the list 
		and returns a map keyed by server-names items and having the value a list of pairs as defined in service-var
		Example: {\"url1\" [{:web: \"1.2\"} {backend: \"1.12\"}], ...}"
  [srv-conf]
  (let [srv-list (:servers srv-conf)
        parsers (:parsers srv-conf)]
  (http/with-connection-pool {:threads (count srv-list) :insecure? true}
		;The anonymous function passed to services-ver-map will call service-ver on the well-formed version URL that will be passed in its single argument.
		;This URL is inside the srv-list that is passed as the last argument of services-ver-map
		;We use apply in order to "slip inside" the argument list, as merge-with takes individual arguments and not a seq as returned by the map function.
    ; see http://clojuredocs.org/clojure_core/1.2.0/clojure.core/apply example #3
    (apply merge-with merge (into [] (map
				(fn [parser] (let [
          service-path (:path parser)
          regexp (:regexp parser)
          service-name (:name parser)]
          (services-ver-map #(service-ver (str % service-path) regexp service-name) srv-list))
				) parsers))))))