(ns overtone.gui.surface.mixer
  (:use
    clojure.stacktrace
    [overtone.gui color]
    [overtone.gui.surface core button monome fader dial])
  (:require [overtone.gui.sg :as sg]))

(defn add-mixer-channel [s i x y]
  (let [dc (get-color :stroke-2)
        bc (get-color :stroke-3)]
    (-> s
      (fader  :volume 0.8 :x x        :y y)
      (dial   :hi     0.5 :x (+ 40 x) :y (+ 0 y)  :color dc)
      (dial   :mid    0.5 :x (+ 40 x) :y (+ 50 y)  :color dc)
      (dial   :low    0.5 :x (+ 40 x) :y (+ 100 y) :color dc)
      (button :cut    1   :x x        :y (+ 170 y) :color bc))))

(def MIXER-WIDTH 550)
(def MIXER-HEIGHT 260)

(defn flip-coin []
  (zero? (rand-int 2)))

(defn add-button [s]
  (let [{:keys [width height]} s
        x (rand-int width)
        y -10
        cx (* 0.5 width)
        cy (* 0.5 height)
        btn (surface-add-widget s (button) x y)
        anim-x (sg/animation (:translate btn) 300 "TranslateX" x cx)
        anim-y (sg/animation (:translate btn) 300 "TranslateY" y cy)]
    (sg/animate anim-x anim-y)))

(defn mixer*
  [width height]
   (try
   (let [s (surface "Mixer" width height)]

     (dotimes [i 4]
       (add-mixer-channel s i (+ 20 (* i 90)) 10))

     (monome s :sequencer nil :rows 4 :columns 4 :x 380 :y 20)

     (comment sg/on-key-pressed (:group s)
       (fn [{:keys [key modifiers]}]
        ;(println "key: " key modifiers)
         (cond
           (= "B" key) (add-button s))))
     s)
     (catch Exception e
       (println "Error: " e)
       (.printStackTrace e))))

(defn mixer
  ([] (mixer MIXER-WIDTH MIXER-HEIGHT))
  ([width height]
   (let [p (promise)]
     (sg/in-swing (deliver p (mixer* width height)))
     @p)))

(defn change-color [widget color]
  (reset! (:color widget) color))

(defn widgets-of-type [m w-type]
  (filter #(= w-type (:type %)) @(:widgets m)))

(defn set-widget-color [m w-type color]
  (try
    (doseq [w (widgets-of-type m w-type)]
      (change-color w color))
    (catch Exception e
      (println "exception in set-widget-color: " e)
      (println (.printStackTrace e)))))

;(on-event :color-changed :mixer-color-change (fn [event] (set-widget-color m :button (:color event))))
