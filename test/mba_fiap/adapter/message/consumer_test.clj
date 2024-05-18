(ns mba-fiap.adapter.message.consumer-test
  (:require [clojure.test :refer :all])
  (:require [clojure.edn :as edn]
            [mba-fiap.adapter.message.consumer :refer [handler-novo-preparo]]
            [mba-fiap.service.preparo :as preparo.service]
            [mba-fiap.use-cases.preparo :as use-cases.preparo]))
