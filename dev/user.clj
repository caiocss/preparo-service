(ns user
  (:require
    [hato.client :as hc]
    [integrant.core :as ig]
    [integrant.repl :as r]
    [integrant.repl.state]
    [mba-fiap.preparo-service :as preparo-service]
    [mba-fiap.adapter.nats :as nats]
    [migratus.core :as migratus])
  (:import (io.nats.client Message MessageHandler Nats Options)))


(integrant.repl/set-prep! #(preparo-service/prep-config :dev))

(def clear r/clear)
(def go r/go)
(def halt r/halt)
(def prep r/prep)
(def init r/init)
(def reset r/reset)
(def reset-all r/reset-all)


(defn portal
  []
  (eval '(do
           (require '[portal.api :as api])
           (add-tap api/submit)
           (api/open))))

(defn migratus
  []
  (:mba-fiap.datasource.migratus/migratus integrant.repl.state/system))


(defn repository
  [repository-key]
  (->> (ig/find-derived integrant.repl.state/system :mba-fiap.repository.repository/repository)
       (filter (fn [[[_ rk]]] (= rk repository-key)))
       first
       second))

(defn consumer-novo-preparo
  [consumer-key]
  (->> (ig/find-derived integrant.repl.state/system :mba-fiap.adapter.message.consumer/novo-preparo)
       (filter (fn [[[_ rk]]] (= rk consumer-key)))
       first
       second))

(comment
  (.listar (repository :repository/preparo))
  (.listar (repository :repository/produto) {})
  (.listar (repository :repository/pedido) {})
  (.criar (repository :repository/preparo)
          {:id-cliente       #uuid "236d3142-e4a7-4c23-976c-34454d8db1fc",
           :produtos
           [#uuid "f11c6b18-89fb-461a-9d76-9c59d9262f23"
            #uuid "4e5ce39e-e30e-48e9-a763-f2a2f2fdcd68"
            #uuid "b800c75e-18af-4d31-a7f1-6f5b3a457903"],
           :numero-do-pedido "2",
           :status           "aguardando pagamento"})

  (.criar (repository :repository/produto)
          {:nome "novo-produto"
           :descricao "descricao"
           :categoria :lanche
           :preco-centavos 400}))



(defn nats-client
  []
  (->> (ig/find-derived integrant.repl.state/system :mba-fiap.adapter.nats/nats)
       (filter (fn [[[_ rk]]] (= rk :nats/nats)))
       first
       second))



(comment
  (with-open [client (nats/nats-client {:url               "nats://66.51.121.86:4222"
                                   :app-name          "preparo-service"
                                   :subjects-handlers {"pedido.*" (consumer-novo-preparo :consumer/novo-preparo)}})]

    (Thread/sleep 500)
    (let []
      (doseq [r (range 1)]
        (Thread/sleep 500)
        (nats/publish client "pedido.novo-preparo" (str {:message "Pedido em preparo" :pedido "123"})))))

  (def client (nats-client))

  (.publish nats-client "pedido.novo-preparo" (str {:id-cliente       #uuid"be9f5a78-2b35-477b-80f1-af2496bce428",
                                                    :produtos
                                                    [#uuid "f11c6b18-89fb-461a-9d76-9c59d9262f23"],
                                                    :numero-do-pedido "2",
                                                    :status           "aguardando pagamento"}
))

  )

(comment
  (hc/get "http://localhost:8080/healthcheck")
  (hc/get "http://localhost:8080/test")
  )

(defn add-migration
  [migration-name]
  (migratus/create (migratus) migration-name))

