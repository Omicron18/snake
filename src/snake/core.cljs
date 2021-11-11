(ns snake.core)

(def canvas (.getElementById js/document "canvas"))
(def ctx (.getContext js/canvas "2d"))

(def size 30)
(def scaling 10)

(.strokeRect ctx 0 0 (inc (* size scaling)) (inc (* size scaling)))

(def state (atom {:snake [{:x 0, :y 0}], :d (list :y inc)}))

(def up (.getElementById js/document "up"))
(def down (.getElementById js/document "down"))
(def left (.getElementById js/document "left"))
(def right (.getElementById js/document "right"))

(defn draw
  ([x y colour]
   (do
     (set! (.-fillStyle ctx) colour)
     (.fillRect ctx
                (inc (* scaling x))
                (inc (* scaling y))
                (dec scaling)
                (dec scaling))))
  ([x y] (draw x y "black")))

(defn draw-apple [x y]
  (draw x y "green"))

(defn clear [x y]
  (draw x y "white"))

(defn clear-all []
  (doall (for [x (range size), y (range size)] (clear x y))))

(defn mutate [key f]
  (swap! state #(update % key f)))

(defn upfn [] (swap! state #(assoc % :d (list :y dec))))
(defn downfn [] (swap! state #(assoc % :d (list :y inc))))
(defn leftfn [] (swap! state #(assoc % :d (list :x dec))))
(defn rightfn [] (swap! state #(assoc % :d (list :x inc))))

(defn toggle-pause []
  (if (:ticker @state)
    (stop!)
    (go!)))

(defn keydown [e]
  (case (.-key e)
    "ArrowUp" (upfn)
    "ArrowDown" (downfn)
    "ArrowLeft" (leftfn)
    "ArrowRight" (rightfn)
    "p" (toggle-pause)))

(.addEventListener up "click" upfn)
(.addEventListener down "click" downfn)
(.addEventListener left "click" leftfn)
(.addEventListener right "click" rightfn)
(.addEventListener js/window "keydown" keydown false)

;;(defn tick []
;;  (do (clear)
;;      (apply mutate (:d @state))
;;      (draw)))

;; (defn grow [s]
;;   (let [snake (:snake s)]
;;     (update s :snake #(conj % (apply update (last %) (:d s))))))

(defn eats? [s]
  (let [head (last (:snake s))]
    (and (= (:x head) (:x (:apple s)))
         (= (:y head) (:y (:apple s))))))

(defn move [s]
  (let [next (apply update (last (:snake s)) (:d s))]
    (if (and (= (:x next) (:x (:apple s)))
             (= (:y next) (:y (:apple s))))
      (update s :snake #(conj % next))
      (update s :snake #(vec (drop 1 (conj % next)))))))

(defn tick []
  (do ;;(clear)
    (let [tail (first (:snake @state))]
      (swap! state move)
      (if (eats? @state)
        (place-apple)
        (clear (:x tail) (:y tail))))
    (let [head (last (:snake @state))]
        (draw (:x head) (:y head)))))

(defn place-apple []
  (do
    (let [x (int (rand size))
          y (int (rand size))]
      (swap! state #(assoc % :apple {:x x, :y y}))
      (draw-apple x y))))

(defn go! []
  (let [t (.setInterval js/window
                        tick
                        500)]
    (swap! state #(assoc % :ticker t))))

(defn stop! []
  (do
    (.clearInterval js/window
                    (:ticker @state))
    (swap! state #(dissoc % :ticker))))

;;  (.clearInterval js/window (:alive @state)))

