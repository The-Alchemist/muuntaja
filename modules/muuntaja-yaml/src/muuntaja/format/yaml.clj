(ns muuntaja.format.yaml
  (:refer-clojure :exclude [format])
  (:require [clj-yaml.core :as yaml]
            [muuntaja.format.core :as core])
  (:import (java.io OutputStream)))

(defn decoder [options]
  (let [options-args (mapcat identity options)]
    (reify
      core/Decode
      (decode [_ data _]
        (apply yaml/parse-string (slurp data) options-args)))))

(defn encoder [options]
  (let [options-args (mapcat identity options)]
    (reify
      core/EncodeToBytes
      (encode-to-bytes [_ data _]
        (.getBytes
          ^String (apply yaml/generate-string data options-args)))
      core/EncodeToOutputStream
      (encode-to-output-stream [_ data _]
        (fn [^OutputStream output-stream]
          (.write output-stream (.getBytes ^String (apply yaml/generate-string data options-args))))))))

(def format
  (core/map->Format
    {:name "application/x-yaml"
     :decoder [decoder {:keywords true}]
     :encoder [encoder]}))
