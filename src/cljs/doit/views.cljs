(ns doit.views
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
                             (reset! content (.-value (.-target val)))
                             )}]
       [:button {:type "input"
                 :on-click (fn [args]
                             (rf/dispatch [::events/add-todo {:content @content}])
                             (reset! content ""))}
        "Add todo"]])))

(defn todos-panel []
  (let [todos (rf/subscribe [::subs/todos])]
    (fn []
      [:div.todos-panel
       [:h3 {:style {:text-align "center"}} "Things to do"]
       (for [todo @todos]
         [:div.todo-item
          "☐ "
          (:content todo)])])))

(defn main-panel []
  [:div.page
   [header]
   [add-todo-form]
   [:br]
   [:hr]

   [todos-panel]])
