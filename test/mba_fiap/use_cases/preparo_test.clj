(ns mba-fiap.use-cases.preparo-test
  (:require [clojure.test :refer :all]
            [mba-fiap.use-cases.preparo :as use-cases.preparo]))


(deftest test-pedido->novo-preparo
  (let [mock-pedido {:id-cliente "236d3142-e4a7-4c23-976c-34454d8db1fc"
                     :numero-do-pedido "123"
                     :produtos ["product1-uuid" "product2-uuid" "product3-uuid"]}
        result (use-cases.preparo/pedido->novo-preparo mock-pedido)]
    (is (= (:id-cliente result) (parse-uuid (:id-cliente mock-pedido))))
    (is (= (:numero-do-pedido result) (:numero-do-pedido mock-pedido)))
    (is (= (:produtos result) (mapv parse-uuid (:produtos mock-pedido))))
    (is (= (:status result) "pronto"))))