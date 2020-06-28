(ns ron-units.core
  #?(:clj (:require [clojure.data.json :as json])))

(defn- format-input
  "converts js json to native json"
  [raw-json]
  #?(:cljs (js->clj raw-json)
     :clj raw-json))

(defn- format-output
  "converts native json to js json"
  [native-json]
  #?(:cljs (clj->js native-json)
     :clj native-json))

(defn json-to-cytoscape-ds
  "Converts a Graph from json to Cytoscape library data format"
  [json-raw-input relation-field]
  (let [json-input (format-input json-raw-input)]
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
          (format-output distinct-output-values))))))

(defn get-strong-vs
  [json-raw unit-name]
  (let [json-input (format-input json-raw)
        unit-obj (first (filter #(= (get % "name") unit-name) json-input))]
        (format-output (get unit-obj "strong-vs"))))

(defn- create-adj-list
  [json-raw]
  (let [json-input (format-input json-raw)
        keys (map #(get % "name") json-input)
        values (map (fn [obj] {:strong-vs (get obj "strong-vs")
                               :weak-vs (get obj "weak-vs")
                               :resources (get obj "resources")}) json-input)]
    (zipmap keys values)))

(defn create-weak-vs-mapping
  "weak-vs listing is not complete, create inverse mapping using strong-vs"
  [json-raw]
  (let [json-input (format-input json-raw)]
    (loop [[current-unit-obj & rest-obj] json-input
           weak-vs-mapping (zipmap
                            (map #(get % "name") json-input)
                            (repeat []))]
      (if (nil? current-unit-obj)
        weak-vs-mapping
        (let [current-unit (get current-unit-obj "name")
              strong-vs (get current-unit-obj "strong-vs")
              new-map-after-adding-neighbors
              (loop [[current-strong-vs & rest-strong-vs] strong-vs
                     weak-vs-mapping-inner weak-vs-mapping]
                (if (nil? current-strong-vs)
                  weak-vs-mapping-inner
                  (let [current-mapping (get weak-vs-mapping-inner current-strong-vs)
                        new-mapping (conj current-mapping current-unit)
                        new-map (assoc weak-vs-mapping-inner current-strong-vs new-mapping)]
                    (recur rest-strong-vs new-map))))]
          (recur rest-obj new-map-after-adding-neighbors))))))

(defn get-weak-vs-bfs-levels
  "Returns list of list of neighbors breadth first"
  ([json-raw unit] (get-weak-vs-bfs-levels json-raw unit 4))
  ([json-raw unit max-depth]
   (let [json-input (format-input json-raw)
         weak-vs-list (create-weak-vs-mapping json-input)]
     (loop [levels []
            queue [unit]
            current-depth 0
            visited #{}]
       (if (or (> current-depth max-depth)
               (empty? queue))
         (format-output levels)
         (let [new-level (set (apply concat (map #(get weak-vs-list %) queue)))
               new-queue (filter #(not (contains? visited %)) new-level)
               new-visited (set (concat visited new-queue))]
           (recur (conj levels new-level)
                  new-queue
                  (+ current-depth 1)
                  new-visited)))))))
