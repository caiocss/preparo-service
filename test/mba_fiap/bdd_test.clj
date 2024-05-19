(ns mba-fiap.bdd-test
  (:require
    [clojure.test :refer :all]
    [malli.generator :as mg]
    [mba-fiap.model.pedido :as pedido]
    [mba-fiap.service.preparo :as preparo.service]
    [mba-fiap.system :as system]
    [fundingcircle.jukebox.alias.cucumber :as cucumber]
    [mba-fiap.adapter.nats :as nats]))

(defn publish-message
  "Publishes a message to a NATS subject."
  [nats-client subject message]
  (nats/publish nats-client subject message))

(defn i-consume-a-new-pedido
  "Consumes a new pedido from the pedido.novo-preparo subscription."
  {:scene/step "I consume a new pedido from pedido.novo-preparo subscription"}
  [_]
    (system/start-pg-container)
    (system/system-start)
    (Thread/sleep 5000)
    (mg/generate pedido/Pedido))

(defn i-insert-a-new-preparo
  "Inserts a new preparo with status em preparo."
  {:scene/step "I insert a new preparo with status em preparo"}
  [pedido]
  (let [repository (get @system/system-state [:mba-fiap.repository.repository/repository :repository/preparo])
        preparo (preparo.service/criar-preparo repository {:id-cliente (:id-cliente pedido)
                                                           :status "em-preparo"
                                                           :numero-do-pedido (:numero-do-pedido pedido)
                                                           :produtos (:produtos pedido)})]
    preparo))

(defn i-publish-preparo
  "Publishes the preparo to the pedido.status subscription."
  {:scene/step "I should publish preparo with status em preparo to pedido.status subscription"}
  [preparo]
  (let [nats-client (get @system/system-state [:mba-fiap.adapter.nats/nats :nats/nats])]
    (nats/publish nats-client "pedido.status" (str preparo)))
  preparo)


(defn run-cucumber []
  (cucumber/-main "-g" "./test/mba_fiap/" "./test/test-resources/"))