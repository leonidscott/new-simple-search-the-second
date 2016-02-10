(ns simple-search.core-hillclimbing
  (:use simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000))

;;; An answer will be a map with (at least) four entries:
;;;   * :instance
;;;   * :choices - a vector of 0's and 1's indicating whether
;;;        the corresponding item should be included
;;;   * :total-weight - the weight of the chosen items
;;;   * :total-value - the value of the chosen items

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

;;; It might be cool to write a function that
;;; generates weighted proportions of 0's and 1's.

(defn score [answer]
  "Takes the total-weight of the given answer unless it's over capacity,
  in which case we return 0."
  (if (> (:total-weight answer)
         (:capacity (:instance answer)))
    0
    (:total-value answer)))

(defn add-score [answer]
  "Computes the score of an answer and inserts a new :score field
  to the given answer, returning the augmented answer."
  (assoc answer :score (score answer)))

(defn -instance [instance choices]
  (let [included (included-items (:items instance) choices)]
    {:instance instance
     :choices choices
     :total-weight (reduce + (map :weight included))
     :total-value (reduce + (map :value included))}))

(defn item-index [choices]
  (map #(- % 1) (filter #(> % 0) (map * (take (count choices) (iterate inc 1)) choices))))

(defn null-item-index [choices]
  (map #(- % 1) (filter #(> % 0) (remove (set (item-index choices)) (map * (take (count choices) (iterate inc 1)) choices)))))

(defn add-item [instance]
  (let [index (rand-int (count (item-index (:choices instance))))]
   (if (== (nth (:choices instance) index) 0)
     (assoc (:choices instance) index 1))))

(defn remove-item [instance]
  (let [index (rand-int (count (item-index (:choices instance))))]
    (if (== (nth (:choices instance) index) 1)
      (assoc (:choices instance) index 0))))

(defn tweaker [instance]
  (-instance (:instance instance)
  (let [tweaked-instance
        ; If there is room in the sac, add something, else remove.
        (if (> (:score instance) 0)
          (add-item instance)
          (remove-item instance))])))

(defn hill-search
  [instance max-tries]
  (loop [current (apply max-key :score (map add-score (repeatedly max-tries #(random-answer instance))))
         tries 0]
    (let [tweaked-instance (add-score (tweaker current))]
      (if (> tries max-tries)
        current
        (if (> (:score tweaked-instance)
               (:score current))
          (recur tweaked-instance (inc tries))
          (recur current (inc tries)))))))

  (time (hill-search knapPI_16_20_1000_1 100000))

  (defn random-search [instance max-tries]
    (apply max-key :score
           (map add-score
                (repeatedly max-tries #(random-answer instance)))))

  (time (random-search knapPI_16_20_1000_1 100000))
