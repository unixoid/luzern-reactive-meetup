(ns huffmann
  (:require [clojure.string :as str]))

; ======================== re-implementing String.count() in Groovy BEGIN
(defn s_count0 [s char from-index]
  (def index (str/index-of s char from-index))
  (if index
    (inc (s_count0 s char (inc index)))
    0))

(defn s_count [s char]
  (s_count0 s char 0))
; ======================== END

(declare topDownSorted bottomUp)

(defn tail2 [maplist]
  (rest (rest maplist)))

(defn topDown [maplist]
  (topDownSorted (sort #(< (:count %1) (:count %2)) maplist)))

(defn topDownSorted [maplist]
  (if (= 1 (count maplist))
    maplist
    (topDown (cons {:items [(nth maplist 0) (nth maplist 1)] :count (+ (:count (nth maplist 0)) (:count (nth maplist 1))) :bits ""} (tail2 maplist)))))

(defn stringToSet [s]
  (set (str/split s #"")))

(defn bottomUpSingleItem [m]
  (if (map? (nth (:items m) 0))
    (bottomUp {:items (:items (nth (:items m) 0)) :bits (:bits m)})
    [m]))

(defn bottomUp [m]
  (if (= 1 (count (:items m)))
    (bottomUpSingleItem m)
    (concat
      (bottomUp {:items [(nth (:items m) 0)] :bits (str (:bits m) "0")})
      (bottomUp {:items [(nth (:items m) 1)] :bits (str (:bits m) "1")}))))

(defn stats [s]
  (map (fn [char] {:items [char] :count (s_count s char) :bits ""}) (stringToSet s)))

(defn bitmap [a]
  (apply hash-map (mapcat (fn [x] [(nth (:items x) 0) (:bits x)]) a)))

(defn huffmann [s]
  (map #(get (bitmap (bottomUp (nth (topDown (stats s)) 0))) %1) (str/split s #"")))

(def s "aaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbcdefghij")
(println (huffmann s))
