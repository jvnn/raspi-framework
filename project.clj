(defproject pi-framework "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [javazoom/jlayer "1.0.1"]
                 [net.async/async "0.1.0"]]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot :all
  :warn-on-reflection true
  :main pi-framework.core)
