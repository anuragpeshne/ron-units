(ns ron-units.core-test
  (:require [clojure.test :refer :all]
            [ron-units.core :refer :all]
            [clojure.data.json :as json]))

(deftest json-to-cytoscape-ds-test
  (testing "directed graph to cytoscape"
    (let [input "[
{
  \"name\": \"a\",
  \"friends\": [\"b\", \"c\"]
},
{
  \"name\": \"b\",
  \"friends\": [\"a\", \"c\"]
},
{
  \"name\": \"d\",
  \"friends\": []
}
]",
          json-input (json/read-str input)
          exp-out [{:data {:id "a"}}
                   {:data {:id "a->b" :source "a" :target "b"}}
                   {:data {:id "a->c" :source "a" :target "c"}}
                   {:data {:id "b"}}
                   {:data {:id "c"}}
                   {:data {:id "b->a" :source "b" :target "a"}}
                   {:data {:id "b->c" :source "b" :target "c"}}
                   {:data {:id "d"}}]
          actual-out (json-to-cytoscape-ds json-input "friends")]
      (is (= (set exp-out) (set actual-out))))))
