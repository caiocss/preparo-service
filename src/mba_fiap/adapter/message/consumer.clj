(ns mba-fiap.adapter.message.consumer)



(defn handler-pedido-pago [message]
  (println "Pedido pronto para iniciar preparo: " message))


(def queues-and-handlers
  {"pedido-pago" #'handler-pedido-pago})