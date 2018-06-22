(ns doit.util)

(defn ->index-by-id [list-map]
  (->> (map (fn [t] [(:id t) t]) list-map)
       (into {})))
