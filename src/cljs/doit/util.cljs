(ns doit.util)

(defn list-map->id-map [list-map]
  (->> (map (fn [t] [(:id t) t]) list-map)
       (into {})))
