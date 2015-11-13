(ns jdbc-demo.core
  (:require
   [clojure.java.jdbc :as j]))

(def ^:dynamic *db*)

(defn list-msgs []
  (j/query *db* "select * from msg"))

(defn insert-msg [id msg]
  (try
    (j/insert! *db* :msg {:id id :body msg})
    (catch Exception e
      (println "ignoring exception: " (.getMessage e)))))
