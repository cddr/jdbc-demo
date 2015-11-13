(ns jdbc-demo.core-test
  (:require [clojure.test :refer :all]
            [jdbc-demo.core :refer :all]
            [clojure.java.jdbc :as j]))

(def test-db-uri "jdbc:postgresql://localhost/jdbc_demo")

(defn call-with-rollback [test-fn]
  (j/with-db-transaction [tx {:connection-uri test-db-uri}]
    (j/db-set-rollback-only! tx)
    (binding [*db* tx]
      (test-fn))))

(use-fixtures :each call-with-rollback)

(deftest test-uniquely
  (do
    ;; this will succeed
    (is (insert-msg 1 "first post"))

    ;; fails due to unique constraint on msg table
    (is (nil? (insert-msg 1 "first post")))

    ;; This assertion fails because it is trying to use a connection
    ;; in which a transaction has been aborted.
    ;;
    ;; I'd like to find a way to make this assertion but still
    ;; rollback so that the next test can be performed with a clean
    ;; slate
    (is [{:id 1 :body "first post"}]
        (j/query *db* "select * from msg"))))
