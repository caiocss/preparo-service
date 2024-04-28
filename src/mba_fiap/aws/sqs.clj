(ns mba-fiap.aws.sqs
  (:require [integrant.core :as ig]
            [mba-fiap.adapter.message.consumer :as consumer])
  (:import [com.amazonaws.services.sqs AmazonSQSClientBuilder]
           [com.amazonaws.auth AWSStaticCredentialsProvider BasicAWSCredentials] 
           [com.amazonaws.services.sqs.model ReceiveMessageRequest DeleteMessageRequest]
           [com.amazonaws.client.builder EndpointConfiguration]))

(defn create-sqs-client [{:keys [access-key secret-key region endpoint]}]
  (AmazonSQSClientBuilder/standard
   .withCredentials (AWSStaticCredentialsProvider. (BasicAWSCredentials. access-key secret-key))
   .withRegion region
   .withEndpointConfiguration (EndpointConfiguration. endpoint region)
   .build))

(defn receive-messages [sqs-client queue]
  (let [request (doto (ReceiveMessageRequest. queue)
                  (.setMaxNumberOfMessages 10)
                  (.setWaitTimeSeconds 20))]
    (.receiveMessage sqs-client request)))

(defn delete-message [sqs-client queue receipt-handle]
  (let [request (DeleteMessageRequest. queue receipt-handle)]
    (.deleteMessage sqs-client request)))

(defn start-consumer [sqs-client]
  (doseq [[queue handler] consumer/queues-and-handlers]
    (future
      (while true
        (let [result (receive-messages sqs-client queue)
              messages (.getMessages result)]
          (doseq [message messages]
            (handler message)
            (delete-message sqs-client queue (.getReceiptHandle message))))))))

(defmethod ig/init-key ::sqs [_ {:keys [access-key secret-key region]}]
  (let [sqs-client (create-sqs-client {:access-key access-key :secret-key secret-key :region region})]
    (println "Creating SQS client")
    (start-consumer sqs-client)
    sqs-client))