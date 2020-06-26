(ns ron-units.core)

(defn json-to-dot
  "Converts a Graph from json to DOT (Graph Description Language)"
  ([json-input relation-field graph-type] (json-to-dot json-input relation-field graph-type "graph"))
  ([json-input relation-field graph-type graph-name]
   (loop [[current-element & rest-elements] json-input
          output []]
     (if-not (or (nil? current-element)
                 (:visited current-element))
       )))
