(ns mba-fiap.adapter.message.consumer
  (:require
    [integrant.core :as ig]
    [mba-fiap.adapter.nats :as nats]))

(defn preparar-pedido  [ctx nats-client msg]
  (prn "Received message on subject")
  (clojure.pprint/pprint ctx)
  (let []
    ;; consume message
    ;; validate schema
    ;; persist in the database
    ;; publish a message inform that the pedido is in preparation
    (println "Received message on subject" msg)
    (nats/publish nats-client "preparo.status" (str {:message "Pedido em preparo" :pedido msg}))))

(defmethod ig/init-key ::preparar-pedido
  [_ ctx]
  (partial preparar-pedido ctx))