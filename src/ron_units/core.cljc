(ns ron-units.core
  #?(:clj (:require [clojure.data.json :as json])))

(defn json-to-cytoscape-ds
  "Converts a Graph from json to Cytoscape library data format"
  [json-raw-input relation-field]
  (let [json-input #?(:cljs (js->clj json-raw-input)
                      :clj json-raw-input)]
    (loop [[current-element & rest-elements] json-input
           output []]
      (if-not (nil? current-element)
        (let [{current-element-name "name"
               relatives relation-field} current-element
              current-element-ds {:data {:id current-element-name}}
              edges (map
                     (fn [sibling] {:data {:id (str current-element-name "->" sibling)
                                           :source current-element-name
                                           :target sibling}})
                     relatives)
              relatives-ds (map (fn [relative] {:data {:id relative}}) relatives)

              new-output (concat (conj output current-element-ds) edges relatives-ds)]
          (recur rest-elements new-output))
        (let [distinct-output-values (set output)]
          #?(:clj distinct-output-values
             :cljs (clj->js (seq distinct-output-values))))))))
