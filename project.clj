(defproject ron-units "0.1.0-SNAPSHOT"
  :description "graph analysis for Rise of Nations Units"
  :url "www.github.com/anuragpeshne/ron-units"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.748"]
                 [org.clojure/data.json "1.0.0"]]
  :plugins [[lein-cljsbuild "1.1.8"]]
  :repl-options {:init-ns ron-units.core}
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to "target/javascripts/main.js"
                                   :optimizations :none
                                   :pretty-print true}}]})
