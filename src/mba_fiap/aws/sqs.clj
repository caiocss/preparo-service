(ns mba-fiap.aws.sqs
  (:require [integrant.core :as ig]
            [cognitect.aws.client.api :as aws]
            [mba-fiap.adapter.message.consumer :as consumer]))

(defn create-sqs-client [{:keys [region]}]
  (aws/client {:api :sqs :region region}))

(defn receive-messages [sqs-client queue]
  (aws/invoke sqs-client {:op :ReceiveMessage :request {:QueueUrl queue :MaxNumberOfMessages 10 :WaitTimeSeconds 20}}))

(defn delete-message [sqs-client queue receipt-handle]
  (aws/invoke sqs-client {:op :DeleteMessage :request {:QueueUrl queue :ReceiptHandle receipt-handle}}))

(defn start-consumer [sqs-client]
  (doseq [[queue handler] consumer/queues-and-handlers]
    (future
      (while true
        (let [result (receive-messages sqs-client queue)
              messages (:Messages result)]
          (doseq [message messages]
            (handler message)
            (delete-message sqs-client queue (:ReceiptHandle message))))))))

(defmethod ig/init-key ::sqs [_ {:keys [region]}]
  (let [sqs-client (create-sqs-client {:region region})]
    (println "Creating SQS client")
    (start-consumer sqs-client)
    sqs-client))