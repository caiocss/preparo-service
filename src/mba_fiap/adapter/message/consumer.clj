(ns mba-fiap.adapter.message.consumer
  (:require
    [clojure.edn :as edn]
    [integrant.core :as ig]
    [mba-fiap.service.preparo :as preparo.service]
    [mba-fiap.use-cases.preparo :as use-cases.preparo]))

(defn handler-novo-preparo
  [ctx nats-client msg]
  (try
    (let [repository (:repository/preparo ctx)
          new-status-subscribe (:topic/novo-status ctx)
          pedido (edn/read-string msg)
          preparo (use-cases.preparo/pedido->novo-preparo pedido)
          result (preparo.service/criar-preparo repository preparo)]
      (.publish nats-client new-status-subscribe (str result)))
    (catch Exception e
      (prn "Error on handler-novo-preparo")
      (prn e)
      (throw e))))

(defmethod ig/init-key ::novo-preparo
  [_ ctx]
  (partial handler-novo-preparo ctx))
