(ns mba-fiap.adapter.message.consumer
  (:require [mba-fiap.adapter.nats :as nats])
  (:import (java.nio.charset StandardCharsets)))

(defn process-pedido-pago [msg]
  (prn "Received message on subject")
  (let [subject (.getSubject msg)
        data (String. (.getData msg))]

    ;; consume message
    ;; validate schema
    ;; persist in the database
    ;; publish a message inform that the pedido is in preparation
    (println "Received message on subject" subject "with data" data)
    (nats/publish nats-client "pedido-em-preparacao" data)))



(comment
  (with-open [c (nats/nats-client {:url               "nats://66.51.121.86:4222"
                                   :app-name          "preparo-service"
                                   :subjects-handlers {"preparo-service.*" #(process-pedido-pago %)}})]

    (doseq [r (range 10)]
      (Thread/sleep 200)
      (nats/publish c "pedido-pago" "Olá")))
  )