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

(deftest ron-data-test
  (testing "test on actual data"
    (let [input (slurp "./data.json")
          json-input (json/read-str input)
          actual-out (json-to-cytoscape-ds json-input "strong-vs")]
      (is (= 88 (count actual-out))))))

(deftest get-strong-vs-test
  (testing "get-strong-vs"
    (let [input (slurp "./data.json")
          json-input (json/read-str input)
          actual-out (get-strong-vs json-input "artillery-weapons")
          exp-out ["buildings"]]
      (is (= actual-out exp-out)))))

(deftest get-weak-vs-bfs-levels-test
  (testing "get breadth first traversal levels"
    (let [input (slurp "./data.json")
          json-input (json/read-str input)
          actual-out (get-weak-vs-bfs-levels json-input "artillery-weapons")
          direct-enemy (get actual-out 0)
          exp-out "light-cavalry"]
      (is (contains? direct-enemy exp-out)))))

(deftest create-weak-vs-mapping-test
  (testing "testing reverse mapping"
    (let [input (slurp "./data.json")
          json-input (json/read-str input)
          actual-out (create-weak-vs-mapping json-input)]
      (is (> 5 (- (count actual-out) (count json-input)))))))
