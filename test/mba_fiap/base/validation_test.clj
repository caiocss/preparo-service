(ns mba-fiap.base.validation-test
  (:require [clojure.test :refer :all]
            [mba-fiap.base.validation :as validation])
  (:import (clojure.lang ExceptionInfo)))


(defn- throw? [e f & args]
  (try
    (apply f args)
    false
    (catch Exception e
      true)))

(deftest test-schema-check
  (let [schema [:map [:name string?]]
        valid-input {:name "valid"}
        invalid-input {:name 123}]
    (testing "schema-check with valid input"
      (is (= valid-input (validation/schema-check schema valid-input))))
    (testing "schema-check with invalid input"
      (is (throw? ExceptionInfo validation/schema-check schema invalid-input)))))