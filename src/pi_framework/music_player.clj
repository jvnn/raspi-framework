;; (c) Jussi Nieminen, 2014

(ns pi-framework.music-player
  (:require [clojure.core.async :refer [go chan <! >! >!!]])
  (:import [javazoom.jl.player.advanced AdvancedPlayer PlaybackListener PlaybackEvent]))


(def PLAY-NEXT "next")
(def STOP-PLAYING "stop")
(def START-PLAYING "play")

(defn- create-listener [control]
  (proxy [PlaybackListener] []
    (playbackFinished [^PlaybackEvent event]
      (println "Playback finished")
      ;;start the next random one
      (>!! control PLAY-NEXT))
    (playbackStarted [^PlaybackEvent event]
      (println "Playback started"))))


(defn- play-file [filename listener]
  (let [fis (java.io.FileInputStream. filename)
        bis (java.io.BufferedInputStream. fis)
        player (AdvancedPlayer. bis)]
    (.setPlayBackListener player listener)
    (.start (Thread. #(doto player (.play) (.close))))
    player))


(defn- play-random [song-list]
  (let [control (chan)]
    (go
      (loop []
        (let [player (play-file (nth song-list (rand-int (count song-list)))
                                (create-listener control))]
          (when-let [ctrl-cmd (<! control)]
            (cond
              (.equals ctrl-cmd STOP-PLAYING)
              (.close player)
              (.equals ctrl-cmd PLAY-NEXT)
              (do
                (.close player)
                (recur)))))))
    control))


(defn start-playback [c]
  (>!! c START-PLAYING))

(defn stop-playback [c]
  (>!! c STOP-PLAYING))

(defn next-song [c]
  (>!! c PLAY-NEXT))


(defn player-setup
  "Initialize the music player and retrieve a channel that can be used to control it"
  [song-list]
  (let [c (chan)]
    (go
      (loop [control nil]
        (when-let [cmd (<! c)]
          (do
            (println "command received:" cmd)
            (cond
              (.equals cmd START-PLAYING)
              (do
                (when (not (nil? control))
                  (>! control STOP-PLAYING))
                (recur (play-random song-list)))
              (.equals cmd STOP-PLAYING)
              (do
                (when (not (nil? control))
                  (>! control STOP-PLAYING))
                (recur nil))
              (.equals cmd PLAY-NEXT)
              (do
                (when (not (nil? control))
                  (>! control PLAY-NEXT))
                (recur control))
              :default
              (recur control)))))
      (println "Channel closed, music player closing."))
    c))
