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
                                 (f (->NATSClient app-name connection nil) (String. (.getData msg))))))
        dispatchers (->> subjects-handlers
                         (mapv (fn [[subject handler]]
                                 (doto (.createDispatcher connection (->dispatcher handler))
                                   (.subscribe subject)))))]
    (->NATSClient app-name connection dispatchers)))

(defmethod ig/init-key ::nats
  [_ config]
  (println "Starting nats client")
  (nats-client config))

(defmethod ig/halt-key! ::nats
  [_ config]
  (.close config))
