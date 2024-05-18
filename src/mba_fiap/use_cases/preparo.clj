(ns mba-fiap.use-cases.preparo)


(defn pedido->novo-preparo
  [{:keys [id-cliente numero-do-pedido produtos]}]
  {:id-cliente       (parse-uuid id-cliente)
   :numero-do-pedido numero-do-pedido
   :produtos         (mapv parse-uuid produtos)
   :status           "em-preparo"})

