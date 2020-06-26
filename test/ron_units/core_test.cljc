(ns ron-units.core-test
  (:require [clojure.test :refer :all]
            [ron-units.core :refer :all]
            [clojure.data.json :as json]))

(deftest undirected-graph-to-dot-test
  (testing "Un-directed graph to DOT"
    (let [input "
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
}",
          json-input (json/read-str input)
          exp-out "
graph friend {
    a -- b -- c;
    d;
}
"
          actual-out (json-to-dot json-input "friends" :undirected "friend")]
      (is (= exp-out actual-out)))))
