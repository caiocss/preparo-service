(ns mba-fiap.datasource.preparo
  (:require
    [honey.sql :as hs]
    [next.jdbc :as jdbc]
    [mba-fiap.repository.repository :as repository]))


(defrecord PreparoDatasource
  [connection]

  repository/Repository
  (criar [_ preparo]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :preparo
                  :values  [{:numero-do-pedido (:numero-do-pedido preparo)
                             :produtos         [:array (:produtos preparo)]
                             :id-cliente       (:id-cliente preparo)
                             :status           (:status preparo)}]})
      {:return-keys true}))

  (buscar
    [_ id]
    (->> {:select [:*]
          :from   :preparo
          :where  [:= :id id]}
         hs/format
         (jdbc/execute-one! connection)))

  (listar
    [_ q]
    (->>
      (merge {:select [:*]
              :from :preparo
              :limit 100} q)
      hs/format
      (jdbc/execute! connection)))

  (atualizar
    [_ data]
    (jdbc/execute!
      connection
      (hs/format {:update :preparo
                  :set data
                  :where [:= :id (:id data)]})
      {:return-keys true}))

  (remover [_ id]
    (->> {:delete-from :preparo
          :where [[:= :id id]]}
         hs/format
         (jdbc/execute-one! connection))))

(defmethod repository/make-repository :preparo
  [{:keys [connection]}]
  (->PreparoDatasource connection))

