(ns mba-fiap.service.preparo
  (:require
    [mba-fiap.base.validation :as validation]
    [mba-fiap.model.preparo :as preparo])
  (:import
    (mba_fiap.repository.repository
      Repository)))


(defn array->vector
  [a]
  (if (coll? a)
    a
    (into [] (.getArray a))))


(defn ^:private ->preparo
  [{:preparo/keys [id id_pedido id_cliente numero_do_pedido produtos status created_at]}]
  {:id id
   :id-pedido id_pedido
   :id-cliente id_cliente
   :numero-do-pedido numero_do_pedido
   :produtos (array->vector produtos)
   :status status
   :created-at created_at})


(defn criar-preparo
  [^Repository repository preparo]
  {:pre [(instance? Repository repository)
         (validation/schema-check preparo/Preparo preparo)]}
  (let [[{:preparo/keys [id_pedido id_cliente numero_do_pedido produtos status]}] (.criar repository preparo)]
    {:id id_pedido
     :id-cliente id_cliente
     :numero-do-pedido numero_do_pedido
     :produtos (array->vector produtos)
     :status status}))


(defn listar-preparos
  [^Repository repository query]
  {:pre [(instance? Repository repository)]}
  (let [result (.listar repository query)
        preparos (mapv ->preparo result)]
    preparos))
