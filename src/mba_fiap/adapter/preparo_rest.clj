(ns mba-fiap.adapter.preparo-rest
  (:require
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.http.ring-middlewares :as middlewares]
    [mba-fiap.adapter.nats :as nats]))

(defn health-check
  [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body {:message "Service is up and running"}})

(defn preparo-routes
  []
  [["/healthcheck" ^:interceptors [(body-params/body-params)
                                    middlewares/params
                                    middlewares/keyword-params]
    {:get `health-check}]])
