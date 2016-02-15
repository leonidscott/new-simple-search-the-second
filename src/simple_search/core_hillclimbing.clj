(ns simple-search.core-hillclimbing
  (:use simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000))

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
  (let [index (rand-int (count (null-item-index (:choices instance))))]
    (assoc (:choices instance) index 1)))

;; Flips a 1 to 0 in :choices
(defn remove-item [instance]
  (let [index (rand-nth (item-index (:choices instance)))]
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

(defn hill-search-with-random-restart
  [instance mutate-function max-tries]
  (def start-instance (add-score (random-answer instance)))
  (println start-instance)
  (loop [current start-instance
         last-best start-instance
         tries 0
         counter 0]
    (let [tweaked-instance (add-score (mutate-function current))]
      (if (> tries max-tries)
        last-best
        (if (> counter 19)
          (if (> (:score current) (:score last-best))
            (do ( println (:score current)) (recur (random-search instance 1) current (inc tries) 0))
            (recur (random-search instance 1) last-best (inc tries) 0))
          (if (> (:score tweaked-instance) (:score current))
            (recur tweaked-instance last-best (inc tries) counter)
            (recur current last-best (inc tries) (inc counter))))))))

;;;(time (hill-search-with-random-restart knapPI_16_20_1000_1 tweaker 100000))

(defn hill-search
  [instance mutate-function max-tries]
  (def start-instance (add-score (random-answer instance)))
  (println start-instance)
  (loop [current start-instance
         tries 0]
    (let [tweaked-instance (add-score (mutate-function current))]
      (if (> tries max-tries)
        current
        (if (> (:score tweaked-instance) (:score current))
          (do (println (:score tweaked-instance)) (recur tweaked-instance (inc tries)))
          (recur current (inc tries)))))))

;;;(time (hill-search knapPI_16_20_1000_1 tweaker-with-rates 10000))
