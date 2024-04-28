(ns mba-fiap.preparo-service

(defn prep-config
  [profile]
  (let [config-map (read-config profile)]
    (ig/load-namespaces config-map)
    (ig/prep config-map)))

(defn start-app
  [profile]
  (log/start-publisher! {:type :console})
  (-> (prep-config profile)
      (ig/init)))

(defn -main
  [& args]
  (let [profile (or (some-> args first keyword) :prod)]
    (println "Running, profile: " profile)
    (start-app profile)))
