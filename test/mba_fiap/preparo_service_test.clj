(ns mba-fiap.preparo-service-test
  (:require [clojure.test :refer :all]
            [hato.client :as hc]
            [mba-fiap.preparo-service :refer :all]
            [mba-fiap.system :as system]))


(deftest test-main
  (system/start-pg-container)
  (system/system-start)
  (Thread/sleep 4000)
  (testing "should start and up the service"
    (let [{:keys [body status]} (hc/get "http://localhost:8080/healthcheck")]
      (is (= 200 status))
      (is (= "{\"message\":\"Service is up and running\"}"
             body))))
  (system/system-stop)
  (system/stop-pg-container))