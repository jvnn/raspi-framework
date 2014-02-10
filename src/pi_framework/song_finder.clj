(ns pi-framework.song-finder
  (:import [java.io File FileFilter]))

(defn- is-music-file [^File item]
  (let [supported #{"mp3"}
        filename (.getName item)
        dot (.lastIndexOf filename ".")
        ending (.substring filename (inc dot))]
    (contains? supported ending)))

(def song-filter
  (proxy [FileFilter] []
    (accept [^File item]
      (and (.isFile item) (is-music-file item)))))

(def dir-filter
  (proxy [FileFilter] []
    (accept [^File item]
      (.isDirectory item))))

(defn create-song-database
  "Browse through the given directory (and subdirectiories) and create a database
   file of all songs that are there"
  [root-path]
  (let [root-file (File. root-path)]
    (loop [dirs (seq (.listFiles root-file dir-filter))
           songs (vec (.listFiles root-file song-filter))
           song-list []]
      (if (empty? dirs)
        (map #(.getAbsolutePath %) (into song-list songs))
        (recur (into (rest dirs) (.listFiles (first dirs) dir-filter))
               (.listFiles (first dirs) song-filter)
               (into song-list songs))))))

