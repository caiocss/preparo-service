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

(defn test-publish-and-consumer
  [request]
  (let [nats (get-in request [:app-context :nats-client])
        msg {:message "Test message"}
        return (.publish nats "lanchonete.preparar-pedido" (str msg))]

    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {:message (str "Message published " return)}}))

(defn preparo-routes
  []
  [["/healthcheck" ^:interceptors [(body-params/body-params)
                                    middlewares/params
                                    middlewares/keyword-params]
    {:get `health-check}]
   ["/test" ^:interceptors [(body-params/body-params)
                                    middlewares/params
                                    middlewares/keyword-params]
    {:get `test-publish-and-consumer}]])
