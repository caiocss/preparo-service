(ns mba-fiap.adapter.message.consumer-test
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.properties :as prop]
    [malli.generator :as mg]
    [clojure.edn :as edn]
    [mba-fiap.adapter.message.consumer :refer [handler-novo-preparo]]
    [mba-fiap.model.pedido :as pedido])
  (:import
    (mba_fiap.adapter.nats INATSClient)
    (mba_fiap.repository.repository Repository)))

(defn- throw? [f & args]
  (try
    (apply f args)
    false
    (catch Exception e
      true)))

(defn mock-nats
  [nats-messages]
  (proxy [INATSClient] []
    (publish
      [subject msg]
      (swap! nats-messages update subject conj msg))))


(defn mock-repository
  [store]
  (proxy [Repository] []
    (listar [_q]
      (let [data @store]
        data))
    (criar [data]
      (swap! store assoc (:numero-do-pedido data) data)
      [#:preparo{:id (random-uuid)
                 :id_cliente (:id-cliente data)
                 :numero_do_pedido (:numero-do-pedido data)
                 :produtos (:produtos data)
                 :status (:status data)}])))


(defspec handler-novo-preparo-test 10
  (prop/for-all [pedido (mg/generator pedido/Pedido)]
    (let [store (atom {})
          nats-messages (atom {})]
      (handler-novo-preparo {:repository/preparo (mock-repository store)
                             :topic/novo-status "status"}
                            (mock-nats nats-messages)
                            (str pedido))
      (and (= (:id-cliente pedido)
              (:id-cliente (edn/read-string (first (get @nats-messages "status")))))
           (= (:numero-do-pedido pedido)
              (:numero-do-pedido (edn/read-string (first (get @nats-messages "status")))))
           (= (:produtos pedido)
              (:produtos (edn/read-string (first (get @nats-messages "status")))))
           (= "pronto"
              (:status (edn/read-string (first (get @nats-messages "status")))))))))

(defspec handler-novo-preparo-error-test 10
  (prop/for-all [pedido (mg/generator pedido/Pedido)]
    (let [store (atom {})]
      (throw? handler-novo-preparo {:repository/preparo (mock-repository store)} nil (str pedido)))))
