(ns simple-search.core-hillclimbing
  (:use simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000
        simple-search.knapsack-examples.knapPI_11_200_1000
        simple-search.knapsack-examples.knapPI_13_200_1000
        simple-search.knapsack-examples.knapPI_16_200_1000
        simple-search.knapsack-examples.knapPI_11_1000_1000
        simple-search.knapsack-examples.knapPI_13_1000_1000
        simple-search.knapsack-examples.knapPI_16_1000_1000))

(defn included-items [items choices]
  "Takes a sequences of items and a sequence of choices and
  returns the subsequence of items corresponding to the 1's
  in the choices sequence."
  (map first
       (filter #(= 1 (second %))
               (map vector items choices))))

(defn random-answer [instance]
  "Construct a random answer for the given instance of the
  knapsack problem."
  (let [choices (repeatedly (count (:items instance))
                            #(rand-int 2))
        new-instance (included-items (:items instance) choices)]
    {:instance instance
     :choices (vec choices)
     :total-weight (reduce + (map :weight new-instance))
     :total-value (reduce + (map :value new-instance))}))

(defn score [answer]
  "Takes the total-weight of the given answer unless it's over capacity,
  in which case we return 0."
  (if (> (:total-weight answer)
         (:capacity (:instance answer)))
    (- (:total-weight answer))
    (:total-value answer)))

(defn add-score [answer]
  "Computes the score of an answer and inserts a new :score field
  to the given answer, returning the augmented answer."
  (assoc answer :score (score answer)))

;;; An answer will be a map with (at least) four entries:
;;;   * :instance
;;;   * :choices - a vector of 0's and 1's indicating whether
;;;        the corresponding item should be included
;;;   * :total-weight - the weight of the chosen items
;;;   * :total-value - the value of the chosen items

(defn random-search [instance max-tries]
  (apply max-key :score
         (map add-score
              (repeatedly max-tries #(random-answer instance)))))

;;(time (random-search knapPI_16_20_1000_1 tweaker 100000))


;;;;; Our Code starts here ;;;;;

(defn make-instance [instance choices]
  (let [included (included-items (:items instance) choices)]
    {:instance instance
     :choices choices
     :total-weight (reduce + (map :weight included))
     :total-value (reduce + (map :value included))}))

;; Gets indicies from :choices with the value '0'
(defn item-index [choices]
  (map #(- % 1) (filter #(> % 0) (map * (range 1 (inc (count choices))) choices))))

;; Gets indicies from :choices with the value '1'
(defn null-item-index [choices]
  (map #(- % 1) (filter #(> % 0) (remove (set (item-index choices)) (set (take (count choices) (iterate inc 0)))))))

;; Flips a 0 to 1 in :choices
(defn add-item [instance]
  (let [index (rand-nth (null-item-index (:choices instance)))]
    (assoc (:choices instance) index 1)))

;; Flips a 1 to 0 in :choices
(defn remove-item [instance]
  (let [index (rand-int (count (item-index (:choices instance))))]
    (assoc (:choices instance) index 0)))

;; Simply enough, adds an item if underweight and removes an item if overweight
(defn tweaker [instance]
  (make-instance (:instance instance)
                 ; If there is room in the sac, add something, else remove.
                 (if (> (:score instance) 0)
                   (add-item instance)
                   (remove-item instance))))

(defn rate-it [tweaked-instance current]
  (/ (- (:total-weight tweaked-instance) (:total-weight current)) (:capacity(:instance current))))

(defn rate-to-tweak [tweaked-instance current]
  (let [rate (rate-it tweaked-instance current)]
    (cond
     (<= rate (/ 1 4)) 1
     (and (> rate (/ 1 4)) (< rate 1)) 2
     :else 3 )))

(defn tweaker-with-rates [instance]
  (def initial (tweaker instance))
  (loop [current initial
         rates 1
         tweak-num (rate-to-tweak initial instance)
         mutate (if (> (:score instance) 0) "add" "remove")]
    (if (== 0 (compare mutate "add"))
      (make-instance (:instance instance) (add-item current))
      (make-instance (:instance instance) (remove-item current)))
    (if (== tweak-num rates)
      current
      (recur current (inc rates) tweak-num mutate))))

(defn combine-two-point-choices [mom-choices dad-choices point1 point2]
  (let [num-items (count mom-choices)
        diff (- point2 point1)
        end (- num-items point2)]
    (vec (concat
          (take point1 mom-choices)
          (take diff (drop point1 dad-choices))
          (take end (drop point2 mom-choices))))))

;; Used this for testing combine-choices -- Working!
;; (let [mom [0 9 0 1 0 2 0 3]
;;       dad [0 1 2 4 3 4 5 6]
;;       num-items (count mom)
;;       point1 2
;;       point2 7
;;       diff (- point2 point1)
;;       end (- (count dad) point2)]
;;   (combine-choices mom dad point1 point2))
;;(vec (concat (take point1 mom) (take diff (drop point1 dad)) (take end (drop point2 mom)))))

(defn two-point-xo [mom-instance dad-instance]
  (let [num-items (count (:choices mom-instance))
        point1 (rand-int (- num-items 1))
        point2 (+ (rand-int (- num-items (+ 1 point1))) (+ 1 point1))]
    ;;(println point1)
    ;;(println point2)
    (make-instance (:instance mom-instance) (combine-two-point-choices (:choices mom-instance) (:choices dad-instance) point1 point2))))

;; Used for testing two-point-xo -- Working!
;; (let [mom (random-search  knapPI_16_20_1000_1 10)
;;       dad (random-search  knapPI_16_20_1000_1 10)]
;;   (println "mom" + mom)
;;   (println "dad" + dad)
;;   (println "final" + (two-point-xo mom dad))
;;   )

;; Used for testing point ints -- Working!
;; (let [mom (random-search  knapPI_16_20_1000_1 10)
;;       num-items (count (:choices mom))
;;       point1 (rand-int num-items)
;;       point2 (+ (rand-int (- num-items (+ 1 point1))) (+ 1 point1))]
;;   (println num-items)
;;    (println point1)
;;   (println point2))


;; If per-vec is less than given percent, grab from mom
;; If per-vec is greater than given percent, grab from dad
(defn get-parent [mom-choices dad-choices per-vec percent index]
  (def index (inc index))
  ;;(println index)
  (if (> percent (nth per-vec index))
    (nth mom-choices index)
    (nth dad-choices index)))

;;(get-parent [0.03, 0.04, 0.06, 0.8, 0.1, 0.3, 0.1] 0.05 [0, 0, 0, 0, 0, 0, 0] [1, 1, 1, 1, 1, 1, 1] 2)

(defn combine-uniform-choices [mom-choices dad-choices per-vec percent]
  (let [num-items (count mom-choices)]
    (def index 0)
    (vec (concat
          (take num-items
                (repeatedly #(get-parent mom-choices dad-choices per-vec percent index)))))))

;;(combine-uniform-choices [0, 0, 0, 0, 0, 0, 0] [1, 1, 1, 1, 1, 1, 1] [0.03, 0.04, 0.06, 0.8, 0.1, 0.3, 0.1] 0.05)

;; Percent needs to be between 0 & 1 (inclusive)
(defn uniform-xo [mom-instance dad-instance percent]
  (let [num-items (count (:choices mom-instance))
        per-vec (take num-items (repeatedly rand))]
    (println per-vec)
    (make-instance (:instance mom-instance) (combine-uniform-choices (:choices mom-instance) (:choices dad-instance) per-vec percent))))

;; Used for testing uniform-xo -- Working!
;; (let [mom (random-search  knapPI_16_20_1000_1 10)
;;       dad (random-search  knapPI_16_20_1000_1 10)]
;;   (println "mom" + mom)
;;   (println "dad" + dad)
;;   (uniform-xo mom dad 0.50))

(defn hill-search-with-random-restart
  [mutate-function instance max-tries]
  (def start-instance (add-score (random-answer instance)))
  (def restart-num (count (:items instance)))
  ;;(println restart-num)
  ;;(println start-instance)
  (loop [current start-instance
         last-best start-instance
         tries 0
         counter 0]
    (let [tweaked-instance (add-score (mutate-function current))]
      (if (> tries max-tries)
        last-best
        (if (> counter restart-num)
          (if (> (:score current) (:score last-best))
            (recur (add-score (random-answer instance)) current (inc tries) 0)
            ;;(do ( println (:score current)) (recur (random-search instance 1) current (inc tries) 0))
            (recur (add-score (random-answer instance)) last-best (inc tries) 0))
          (if (> (:score tweaked-instance) (:score current))
            (recur tweaked-instance last-best (inc tries) counter)
            (recur current last-best (inc tries) (inc counter))))))))

;;(time (hill-search-with-random-restart tweaker knapPI_16_1000_1000_3 10000))

(defn hill-search
  [mutate-function instance max-tries]
  (def start-instance (add-score (random-answer instance)))
  ;;(println start-instance)
  (loop [current start-instance
         tries 0]
    (let [tweaked-instance (add-score (mutate-function current))]
      (if (> tries max-tries)
        current
        (if (> (:score tweaked-instance) (:score current))
          (recur tweaked-instance (inc tries))
          ;;(do (println (:score tweaked-instance)) (recur tweaked-instance (inc tries)))
          (recur current (inc tries)))))))

;;(time (hill-search tweaker-with-rates knapPI_16_1000_1000_1 1000))
