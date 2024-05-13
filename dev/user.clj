(ns user
  (:require
    [integrant.repl :as r]
    [integrant.repl.state]
    [mba-fiap.preparo-service :as preparo-service]))


(integrant.repl/set-prep! #(preparo-service/prep-config :dev))

(def clear r/clear)
(def go r/go)
(def halt r/halt)
(def prep r/prep)
(def init r/init)
(def reset r/reset)
(def reset-all r/reset-all)