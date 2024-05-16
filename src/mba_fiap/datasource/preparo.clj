(ns mba-fiap.datasource.preparo
  (:require [honey.sql :as hs]
            [next.jdbc :as jdbc]
            [mba-fiap.repository.repository :as repository])
  (:import [java.io Closeable]))

(defn uuid-parseable? [s]
  (boolean (try
             (parse-uuid s)
             (catch Exception _e
               false))))

(defrecord PreparoDatasource [connection]
  repository/Repository
  (criar [_ preparo]
    (jdbc/execute!
      connection
      (hs/format {:insert-into :preparo
                  :values [{:cpf (:cpf preparo)
                            :nome (:nome preparo)
                            :email (:email preparo)}]})
      {:return-keys true}))
  (buscar [_ id-or-cpf]
   (let [where-clause (cond
                        (uuid? id-or-cpf)  [:= :id id-or-cpf]
                        (uuid-parseable? id-or-cpf) [:= :id (parse-uuid id-or-cpf)]
                        :else [:= :cpf id-or-cpf])]
    (->> {:select [:*]
          :from :preparo
          :where where-clause}
         hs/format
         (jdbc/execute-one! connection))))
  (listar [_ q]
    (->> (merge {:select [:*]
                 :from :preparo
                 :limit 100} q)
         hs/format
         (jdbc/execute! connection)))

  (atualizar [_ data]
    (->> {:update :preparo
          :set data
          :where [:= :id (:id data)]}
         hs/format
         (jdbc/execute! connection)))

  (remover [_ id]
    (->> {:delete-from :preparo
          :where [[:= :id id]]}
         hs/format
         (jdbc/execute-one! connection))))

(defmethod repository/make-repository :preparo
  [{:keys [connection]}]
  (->PreparoDatasource connection))

