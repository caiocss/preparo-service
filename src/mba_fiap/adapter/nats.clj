(ns mba-fiap.adapter.nats
  (:require [integrant.core :as ig])
  (:import (io.nats.client Message MessageHandler Nats Options)
           (java.io Closeable)
           (java.nio.charset StandardCharsets)))



(defprotocol INATSClient
  (publish [_ subject msg]))

(defrecord NATSClient [app-name connection dispatchers]
  Closeable
  (close [_]
    (run! #(.closeDispatcher connection %) dispatchers)
    (.close connection))

  INATSClient
  (publish [_ subject msg]
    (let [subject (str app-name "." subject)
          reply-to (str subject ".reply")]

      (prn reply-to)
      (.publish connection
                subject
                reply-to
                (.getBytes msg StandardCharsets/UTF_8)))))

(defn nats-client [{:keys [app-name url subjects-handlers]}]
  (let [connection (Nats/connect (-> (Options/builder)
                                     (.server url)
                                     (.build)))
        ->dispatcher (fn [f] (reify MessageHandler
                               (^void onMessage [_ ^Message msg]
                                 (f msg))))
        dispatchers (->> subjects-handlers
                         (mapv (fn [[subject handler]]
                                 (doto (.createDispatcher connection (->dispatcher handler))
                                   (.subscribe subject)))))]
    (->NATSClient app-name connection dispatchers)))

(defmethod ig/init-key :mba-fiap.adapter.nats/nats
  [_ {:keys [app-name url subjects-handlers]}]
  (nats-client {:app-name app-name
                :url url
                :subjects-handlers subjects-handlers}))

(defmethod ig/halt-key! :mba-fiap.adapter.nats/nats
  [_ nats-client]
  (.close nats-client))


(comment
  (with-open [c (nats-client {:url               "nats://66.51.121.86:4222"
                              :app-name          "testing"
                              :subjects-handlers {"testing.*" #(prn (.getSubject %) " " (String. (.getData %)) "----" (bean %))}})]

    (doseq [r (range 10)]
      (Thread/sleep 200)
      (publish c r "YAMETE KUDASAI")))
  )