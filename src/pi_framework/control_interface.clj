;; (c) Jussi Nieminen, 2014

(ns pi-framework.control-interface
  (:use [net.async.tcp]
        [clojure.core.async :refer [go chan <! >!]]))


(defn control-interface-setup
  "Start a TCP server that accepts control commands for the framework"
  [port]
  (let [acceptor (accept (event-loop) {:port port})
        c (chan)]
    (go
      (loop []
        (when-let [server (<! (:accept-chan acceptor))]
          (loop []
            (when-let [msg (<! (:read-chan server))]
              (when-not (keyword? msg)
                (>! c (String. msg)))
              (recur))))
        (recur)))
    c))
