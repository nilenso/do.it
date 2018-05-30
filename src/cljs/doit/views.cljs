(ns doit.views
  (:refer-clojure :exclude [subs])
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [doit.subs :as subs]
            [doit.events :as events]))

(defn header []
  [:div.header
   [:h1 "DO•IT"]])

(defn add-todo-form []
  (let [content (reagent/atom "")]
    (fn []
      [:div.form
       [:input {:type "text"
                :value @content
                :on-change (fn [val]
                             (reset! content (.-value (.-target val))))}]
       [:button {:type "input"
                 :on-click (fn [args]
                             (rf/dispatch [::events/add-todo {:content @content}])
                             (reset! content ""))}
        "Add todo"]])))

(defn remaining-todos-panel []
  (let [todos (rf/subscribe [::subs/remaining-todos])]
    (fn []
      [:div.remaining-todos-panel
       [:h3 {:style {:text-align "center"}} "Tasks to do"]
       [:div.remaining-todos
        (for [todo @todos]
          ^{:key (:id todo)}
          [:div
           [:i.check-box.far.fa-square
            {:on-click (fn [args] (rf/dispatch [::events/mark-done (:id todo)]))}]
           (:content todo)])]])))

(defn completed-todos-panel []
  (let [todos (rf/subscribe [::subs/completed-todos])]
    (fn []
      [:div.completed-todos-panel
       [:h3 {:style {:text-align "center"}} "Tasks completed"]
       [:div.completed-todos
        (for [todo @todos]
          ^{:key (:id todo)}
          [:div
           [:i.check-box.far.fa-check-square
            {:on-click (fn [args] (rf/dispatch [::events/mark-undone (:id todo)]))}]
           (:content todo)])]])))

(defn main-panel []
  [:div.page
   [header]
   [add-todo-form]
   [:br]
   [:hr]
   [remaining-todos-panel]
   [:br]
   [:hr]
   [completed-todos-panel]])
