(ns what-runs-where.test.handler
  (:use clojure.test
        ring.mock.request  
        what-runs-where.handler
        ring.adapter.jetty))

;first load the test config
(load-config "test/what_runs_where/test/config-test.properties")

(deftest test-app
  (testing "/version URL"
     ;start web server
     (def srv (what-runs-where.handler/-main "-c" "test/what_runs_where/test/config-test.properties" "-p" "40008"))
    (let [api-response (app (request :get "/api/services"))
          clojure-ver (last (re-find #"\"Clojure\":\"([\d.]*)\"" (:body api-response)))]
      (is (= clojure-ver (clojure-version))))
    ;stop web server
    (.stop srv)
    )
  
  (testing "main route"
    (let [response (app (request :get "/"))
          service-count (re-find #"2 service\(s\)" (:body response))]
      (is (= (:status response) 200))
      (is ((complement nil?) service-count))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      ))
  (testing "/version URL"
    (let [api-response (app (request :get "/version"))
          clojure-ver (last (re-find #"\"clojure\": \"([\d.]*)\"" (:body api-response)))]
      (is (= (:status api-response) 200))
        (is (= clojure-ver (clojure-version))))))