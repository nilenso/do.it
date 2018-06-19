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

(defn add-todo-form [listid]
  (let [content (reagent/atom "")]
    (fn []
      [:div.form
       [:input {:type      "text"
                :value     @content
                :on-change (fn [val]
                             (reset! content (.-value (.-target val))))}]
       [:button {:type     "input"
                 :on-click (fn [args]
                             (rf/dispatch [::events/add-todo {:content @content :listid listid}])
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

(defn remaining-todos-panel [listid]
  (let [todos (rf/subscribe [::subs/remaining-todos listid])]
    (fn []
      [:div.remaining-todos
       (for [todo @todos]
         ^{:key (:id todo)}
         [:div.todo-row
          [:i.delete-btn.far.fa-trash-alt
           {:on-click (fn [_] (rf/dispatch [::events/delete-todo (:id todo)]))}]
          [:i.check-box.far.fa-square
           {:on-click (fn [_] (rf/dispatch [::events/mark-done (:id todo)]))}]
          [editable-todo (:id todo)]])])))

(defn completed-todos-panel [listid]
  (let [todos (rf/subscribe [::subs/completed-todos listid])]
    (fn []
      [:div.completed-todos
       (for [todo @todos]
         ^{:key (:id todo)}
         [:div.todo-row
          [:i.delete-btn.far.fa-trash-alt
           {:on-click (fn [_] (rf/dispatch [::events/delete-todo (:id todo)]))}]
          [:i.check-box.far.fa-check-square
           {:on-click (fn [_] (rf/dispatch [::events/mark-undone (:id todo)]))}]
          [editable-todo (:id todo)]])])))

(defn todos-panel [listid]
  [:div
   [remaining-todos-panel listid]
   [:br]
   [:hr]
   [completed-todos-panel listid]
   [:br]
   [:hr]
   [add-todo-form listid]])

(defn add-todo-list-form []
  (let [name (reagent/atom "")]
    (fn []
      [:div.form
       [:input {:type      "text"
                :value     @name
                :on-change (fn [val]
                             (reset! name (.-value (.-target val))))}]
       [:button {:type     "input"
                 :on-click (fn [args]
                             (rf/dispatch [::events/add-todo-list {:name @name}])
                             (reset! name ""))}
        "Add Todo List"]])))

(defn lists-panel []
  (let [todo-lists (rf/subscribe [::subs/todo-lists])]
    (fn []
      [:div
       [add-todo-list-form]
       [:div.lists-panel
        (for [todo-list @todo-lists]
          ^{:key (:id todo-list)}
          [:div.list-box
           [:h4.list-name (:name todo-list)]
           [todos-panel (:id todo-list)]])]])))

(defn sign-in-panel []
  [:div.sign-in-panel
   [:a {:href     "#"
         :on-click auth/sign-in}
    [:img.sign-in-btn-img {:src "/images/btn_google_signin.png"
                           :alt "sign in with Google"}]]])

(defn sign-out-panel []
  [:div.sign-out-panel
   [:a {:href "#"
        :on-click (fn [_] (auth/sign-out))}
    "Sign Out"]])

(defn main-panel []
  (let [auth-token (rf/subscribe [::subs/auth-token])]
    (fn []
      [:div.page
       [header]
       (if-not @auth-token
         [sign-in-panel]
         [:div
          [sign-out-panel]
          [lists-panel]])])))
