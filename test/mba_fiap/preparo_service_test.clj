(ns mba-fiap.preparo-service-test
  (:require [clojure.test :refer :all]
            [hato.client :as hc]
            [malli.generator :as mg]
            [mba-fiap.model.pedido :as pedido]
            [mba-fiap.preparo-service :refer :all]
            [mba-fiap.service.preparo :as preparo.service]
            [mba-fiap.system :as system]
            [mba-fiap.repository.repository]))

(defn system-fixture [f]
  (let [_system (system/system-start)]
    (try
      (f)
      (catch Exception e
        (prn e))
      (finally (system/system-stop)))))


(use-fixtures :once system-fixture)

(deftest test-main
  (testing "should start and up the service"
    (let [{:keys [status]} (hc/get "http://localhost:8080/healthcheck")]
      (is (= 200 status)))))

(deftest nats-test
  (testing "should start and up the service"
    (let [pedido (mg/generate pedido/Pedido)
          nats-client (get @system/system-state [:mba-fiap.adapter.nats/nats :nats/nats])
          repository (get @system/system-state [:mba-fiap.repository.repository/repository :repository/preparo])
          _ (.publish nats-client "novo-preparo" (str pedido))
          _ (Thread/sleep 5000)
          preparos (preparo.service/listar-preparos repository {})]
     (is (not (empty? preparos))))))