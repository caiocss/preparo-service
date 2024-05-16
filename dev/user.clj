(ns user
  (:require
    [hato.client :as hc]
    [integrant.repl :as r]
    [integrant.repl.state]
    [mba-fiap.preparo-service :as preparo-service]
    [mba-fiap.adapter.nats :as nats]))


(integrant.repl/set-prep! #(preparo-service/prep-config :dev))

(def clear r/clear)
(def go r/go)
(def halt r/halt)
(def prep r/prep)
(def init r/init)
(def reset r/reset)
(def reset-all r/reset-all)

(defn preparar-pedido
  []
  (:mba-fiap.adapter.message.consumer/preparar-pedido  integrant.repl.state/system))

(defn nats-client
  []
  (:mba-fiap.adapter.nats/nats integrant.repl.state/system))



(comment
  (with-open [c (nats/nats-client {:url               "nats://66.51.121.86:4222"
                                   :app-name          "preparo-service"
                                   :subjects-handlers {"preparo-service.*" #(prn "hi")}})]

    (let [client (nats-client)]
      (doseq [r (range 2)]
        (Thread/sleep 200)
        (nats/publish client "lanchonete.preparar-pedido" "Olá"))))
  )



(comment
  (hc/get "http://localhost:8080/healthcheck")
  (hc/get "http://localhost:8080/test")
  )

