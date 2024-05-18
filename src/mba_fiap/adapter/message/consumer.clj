(ns mba-fiap.adapter.message.consumer
  (:require
    [clojure.edn :as edn]
    [integrant.core :as ig]
    [mba-fiap.service.preparo :as preparo.service]
    [mba-fiap.use-cases.preparo :as use-cases.preparo]))


(defn handler-novo-preparo  [ctx nats-client msg]
  (prn "Received message on subject")
  (tap> {:from "handler-novo-preparo"
         :msg msg})
  (let [repository (:repository/pedido ctx)
        pedido (edn/read-string msg)
        preparo (use-cases.preparo/pedido->novo-preparo pedido)
        result (preparo.service/criar-preparo repository preparo)]
    (.publish nats-client "pedido.status" (str result))))

(defmethod ig/init-key ::novo-preparo
  [_ ctx]
  (partial handler-novo-preparo ctx))

(defn testing
  [_ event]
  (tap> {:from "testing"
         :event event}))

(defmethod ig/init-key ::test [_ _]
  (partial testing))