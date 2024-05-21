(ns mba-fiap.use-cases.preparo)

(defn ->uuid [string]
  (if (uuid? string)
    string
    (parse-uuid string)))

(defn pedido->novo-preparo
  [{:keys [id id-cliente numero-do-pedido produtos]}]
  {:id-pedido        (->uuid id)
   :id-cliente       (->uuid id-cliente)
   :numero-do-pedido numero-do-pedido
   :produtos         (mapv ->uuid produtos)
   :status           "pronto"})

