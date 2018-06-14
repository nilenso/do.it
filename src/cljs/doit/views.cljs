(ns doit.views
  (:refer-clojure :exclude [subs])
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [doit.subs :as subs]
            [doit.auth :as auth]
            [doit.config :as config]
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

(defn editable-todo [id]
  (let [todo (rf/subscribe [::subs/todo id])]
    (fn []
      [:input.todo {:type      "text"
                    :value     (:content @todo)
                    :on-change (fn [val]
                                 (let [new-content (.-value (.-target val))]
                                   (rf/dispatch [::events/update-todo (assoc @todo :content new-content)])))}])))

(defn remaining-todos-panel []
  (let [todos (rf/subscribe [::subs/remaining-todos])]
    (fn []
      [:div.remaining-todos-panel
       [:h3 {:style {:text-align "center"}} "Tasks to do"]
       [:div.remaining-todos
        (for [todo @todos]
          ^{:key (:id todo)}
          [:div.todo-row
           [:i.check-box.far.fa-square
            {:on-click (fn [args] (rf/dispatch [::events/mark-done (:id todo)]))}]
           [editable-todo (:id todo)]])]])))

(defn completed-todos-panel []
  (let [todos (rf/subscribe [::subs/completed-todos])]
    (fn []
      [:div.completed-todos-panel
       [:h3 {:style {:text-align "center"}} "Tasks completed"]
       [:div.completed-todos
        (for [todo @todos]
          ^{:key (:id todo)}
          [:div.todo-row
           [:i.check-box.far.fa-check-square
            {:on-click (fn [args] (rf/dispatch [::events/mark-undone (:id todo)]))}]
           [editable-todo (:id todo)]])]])))

(defn todos-panel []
  [:div
   [:a {:href "#"
        :on-click (fn [_] (auth/sign-out))}
    "Sign Out"]
   [add-todo-form]
   [:br]
   [:hr]
   [remaining-todos-panel]
   [:br]
   [:hr]
   [completed-todos-panel]])

(defn sign-in-panel []
  [:div.sign-in-panel
   [:a {:href     "#"
         :on-click auth/sign-in}
    [:img.sign-in-btn-img {:src "/images/btn_google_signin.png"
                           :alt "sign in with Google"}]]])

(defn main-panel []
  (let [auth-token (rf/subscribe [::subs/auth-token])]
    (fn []
      [:div.page
       [header]
       (if-not @auth-token
         [sign-in-panel]
         [todos-panel])])))
