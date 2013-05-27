(ns what-runs-where.test.handler
  (:use clojure.test
        ring.mock.request  
        what-runs-where.handler))

;first load the test config
(load-config "test/what_runs_where/test/config-test.properties")

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))
          service-count (re-find #"2 service\(s\)" (:body response))]
      (is (= (:status response) 200))
      (is ((complement nil?) service-count))))
  (testing "api"
    (let [api-response (app (request :get "/api/services"))
          clojure-ver (re-find #"\"Clojure\":\"([\d.]*)\"" (:body api-response))]
      (is (= clojure-ver (clojure-version)))))
  
  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))