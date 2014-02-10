;; (c) Jussi Nieminen, 2014

(ns pi-framework.core
  (:use [pi-framework music-player control-interface song-finder]
        [clojure.core.async :refer [go <!]])
  (:gen-class))


(defn main-control-loop
  "Parse messages from the control interface and act accordingly."
  [music-path]
  (let [ctrl-c-in (control-interface-setup 8989)
        music-c (player-setup (create-song-database music-path))]
    (go
      (loop []
        (when-let [cmd (<! ctrl-c-in)]
          (println "Command received:" cmd)
          (cond
            (.equals cmd "play-music")
            (start-playback music-c)
            (.equals cmd "next-song")
            (next-song music-c)
            (.equals cmd "stop-music")
            (stop-playback music-c)
            :default
            (println "Invalid command:" cmd))
          (recur))))))


(defn -main
  []
  (main-control-loop "/home"))
