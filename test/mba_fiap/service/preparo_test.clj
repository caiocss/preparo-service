(ns mba-fiap.service.preparo-test
  (:require
    [clojure.test :refer :all]
    [clojure.walk :as walk]
    [malli.generator :as mg]
    [mba-fiap.service.preparo :as preparo.service]
    [clojure.test.check.properties :as prop]
    [mba-fiap.model.preparo :as preparo]
    [clojure.test.check.clojure-test :refer [defspec]])
  (:import
    [mba_fiap.repository.repository Repository]))

(defn keyword-to-namespaced-keyword [namespace m]
  (walk/postwalk
    (fn [x]
      (if (keyword? x)
        (keyword namespace (name x))
        x))
    m))

(defn mock-repository [store]
  (proxy [Repository] []
    (listar [_q]
      (let [data @store]
        data))
    (criar [data]
      (swap! store assoc (:id-cliente data) data)
      [#:preparo{:id (random-uuid)
                :id-cliente (:id-cliente data)
                :numero-do-pedido (:numero_do_pedido data)
                :produtos (:produtos data)
                 :status (:status data)}])))

(defn gen-preparos []
  (let [preparo-schema [:vector {:min 1} preparo/Preparo]]
    (mg/generator preparo-schema)))

(defspec all-valid-preparos-inserted 2
  (prop/for-all [preparo (mg/generator preparo/Preparo)]
    (let [store (atom {})
          repository (mock-repository store)]
      (boolean (preparo.service/criar-preparo repository preparo)))))

(defspec listar-preparos-test 1
  (prop/for-all [preparos (gen-preparos)]
    (let [store (atom (mapv #(keyword-to-namespaced-keyword "preparo" %) preparos))
          repository (mock-repository store)]
      (boolean (preparo.service/listar-preparos repository {})))))