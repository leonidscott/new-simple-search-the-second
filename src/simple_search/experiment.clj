(ns simple-search.experiment
  (:require [simple-search.core-hillclimbing :as core])
  (:use simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000
        simple-search.knapsack-examples.knapPI_11_200_1000
        simple-search.knapsack-examples.knapPI_13_200_1000
        simple-search.knapsack-examples.knapPI_16_200_1000
        simple-search.knapsack-examples.knapPI_11_1000_1000
        simple-search.knapsack-examples.knapPI_13_1000_1000
        simple-search.knapsack-examples.knapPI_16_1000_1000))

(defn run-experiment
  [searchers problems num-replications max-evals]
  (println "Search Problem Max_Evals Run Score")
  (for [searcher searchers
        p problems
        n (range num-replications)]
    (let [answer (searcher p max-evals)]
      {:searcher searcher
       :problem p
       :max-evals max-evals
       :run-number n
       :answer answer})))

(defn print-experimental-results
  [results]
  (doseq [result results]
    (println (:label (meta (:searcher result)))
             (:label (:problem result))
             (:max-evals result)
             (:run-number result)
             (:score (:answer result)))))

;; This really shouldn't be necessary, as I should have included the labels
;; in the maps when generated the problem files. Unfortunately I only just
;; realized that, and it's easier to do this than have everyone merge in a
;; new set of problem files to all your projects.
(defn get-labelled-problem
  "Takes the name of a problem (as a string) and returns the actual
  problem instance (as a map) with the name added to the map under
  the :label key."
  [problem-name]
  (let [problem (var-get (resolve (symbol problem-name)))]
    (assoc problem :label problem-name)))

(defn -main
  "Runs a set of experiments with the number of repetitions and maximum
  answers (tries) specified on the command line.

  To run this use something like:

  lein run -m simple-search.experiment 30 1000

  where you replace 30 and 1000 with the desired number of repetitions
  and maximum answers.
  "
  [num-repetitions max-answers]
  ; This is necessary to "move" us into this namespace. Otherwise we'll
  ; be in the "user" namespace, and the references to the problems won't
  ; resolve propertly.
  (ns simple-search.experiment)
  (print-experimental-results
   (run-experiment [(with-meta
                      (partial core/population-search core/uniform-xo 100 5)
                      {:label "U_XO_5"})
                    (with-meta
                      (partial core/population-search core/two-point-xo 100 5)
                      {:label "TP_OX_5"})
                    (with-meta
                      (partial core/population-search core/tweaker-xo 100 5)
                      {:label "Tweak_5"})
                    (with-meta
                      (partial core/population-search core/uniform-xo 100 10)
                      {:label "U_XO_10"})
                    (with-meta
                      (partial core/population-search core/two-point-xo 100 10)
                      {:label "TP_OX_10"})
                    (with-meta
                      (partial core/population-search core/tweaker-xo 100 10)
                      {:label "Tweak_10"})
                    (with-meta
                      (partial core/population-search core/uniform-xo 100 25)
                      {:label "U_XO_25"})
                    (with-meta
                      (partial core/population-search core/two-point-xo 100 25)
                      {:label "TP_OX_25"})
                    (with-meta
                      (partial core/population-search core/tweaker-xo 100 25)
                      {:label "Tweak_25"})
                    (with-meta
                      (partial core/population-search core/uniform-xo 100 50)
                      {:label "U_XO_50"})
                    (with-meta
                      (partial core/population-search core/two-point-xo 100 50)
                      {:label "TP_OX_50"})
                    (with-meta
                      (partial core/population-search core/tweaker-xo 100 50)
                      {:label "Tweak_50"})]
                   (map get-labelled-problem
                        [;;"knapPI_11_20_1000_4" "knapPI_13_20_1000_4" "knapPI_16_20_1000_4"
                         ;;"knapPI_11_200_1000_4" "knapPI_13_200_1000_4" "knapPI_16_200_1000_4"])
                   "knapPI_16_1000_1000_3"])
                   (Integer/parseInt num-repetitions)
                   (Integer/parseInt max-answers)))
  (shutdown-agents))

;population-search
;;(:score (same-population-search uniform-xo true 40 5 knapPI_16_20_1000_3 10000))
