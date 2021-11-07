(ns snake.core)

(def canvas (.getElementById js/document "canvas"))
(def ctx (.getContext js/canvas "2d"))

(def state (atom {:snake [{:x 0, :y 0}], :d (list :y inc)}))

(def up (.getElementById js/document "up"))
(def down (.getElementById js/document "down"))
(def left (.getElementById js/document "left"))
(def right (.getElementById js/document "right"))

(defn clear []
  (do
    (set! (.-fillStyle ctx) "white")
    (.fillRect ctx (* 10 (:x @state)) (* 10 (:y @state)) 10 10)))

(defn draw [x y]
  (do
    (set! (.-fillStyle ctx) "black")
    (.fillRect ctx (* 10 x) (* 10 y) 10 10)))

(defn mutate [key f]
  (swap! state #(update % key f)))

;; (defn upfn [] (do (clear) (mutate :y dec) (draw)))
;; (defn downfn [] (do (clear) (mutate :y inc) (draw)))
;; (defn leftfn [] (do (clear) (mutate :x dec) (draw)))
;; (defn rightfn [] (do (clear) (mutate :x inc) (draw)))

(defn upfn [] (swap! state #(assoc % :d (list :y dec))))
(defn downfn [] (swap! state #(assoc % :d (list :y inc))))
(defn leftfn [] (swap! state #(assoc % :d (list :x dec))))
(defn rightfn [] (swap! state #(assoc % :d (list :x inc))))

(.addEventListener up "click" upfn)
(.addEventListener down "click" downfn)
(.addEventListener left "click" leftfn)
(.addEventListener right "click" rightfn)

;;(defn tick []
;;  (do (clear)
;;      (apply mutate (:d @state))
;;      (draw)))

(defn grow [s]
  (let [snake (:snake s)]
    (update s :snake #(conj % (apply update (last %) (:d s))))))

(defn tick []
  (do ;;(clear)
      (swap! state grow)
      (let [tail (last (:snake @state))]
        (draw (:x tail) (:y tail)))))

(.setInterval js/window
              tick
              1000)


