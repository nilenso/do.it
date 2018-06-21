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
   [:h1 "DO•IT"]
   [:a {:href "#"} "sign-out"]])

(defn add-todo [listid]
  (let [content (reagent/atom "")]
    (fn []
      [:div.add-object
       [:input {:type        "text"
                :placeholder "Enter a new todo..."
                :value       @content
                :on-change   (fn [val]
                               (reset! content (.-value (.-target val))))}]
       [:button {:type     "input"
                 :on-click (fn [args]
                             (rf/dispatch [::events/add-todo {:content @content :listid listid}])
                             (reset! content ""))}
        [:i {:class "fas fa-plus"}]]])))

(defn set-todo-height [id]
  (when-let [parent-element (.getElementById js/document (str "todo-" id))]
    ;; Change height of the text
    (set! (.-height (.-style parent-element)) "auto")
    (let [new-height (.-scrollHeight parent-element)]
      (prn new-height)
      (set! (.-height (.-style parent-element)) (str new-height "px")))))

(defn editable-todo [id]
  (let [todo (rf/subscribe [::subs/todo id])]
    (fn []
      (set-todo-height id)
      [:textarea.todo {:type      "text"
                       :id        (str "todo-" id)
                       :value     (:content @todo)
                       :on-change (fn [val]
                                    (let [new-content (.-value (.-target val))]
                                      (rf/dispatch [::events/update-todo (assoc @todo :content new-content)])))}])))

(defn remaining-todos-panel [listid]
  (let [todos (rf/subscribe [::subs/remaining-todos listid])]
    (fn []
      [:div.items-remaining
       (for [todo @todos]
         ^{:key (:id todo)}
         [:div.item
          [:i.delete-btn.far.fa-trash-alt
           {:on-click (fn [_] (rf/dispatch [::events/delete-todo (:id todo)]))}]
          [:i.check-box.far.fa-square
           {:on-click (fn [_] (rf/dispatch [::events/mark-done (:id todo)]))}]
          [editable-todo (:id todo)]])])))

(defn completed-todos-panel [listid]
  (let [todos (rf/subscribe [::subs/completed-todos listid])]
    (fn []
      [:div.items-completed
       (for [todo @todos]
         ^{:key (:id todo)}
         [:div.item
          [:i.delete-btn.far.fa-trash-alt
           {:on-click (fn [_] (rf/dispatch [::events/delete-todo (:id todo)]))}]
          [:i.check-box.fas.fa-check-square
           {:on-click (fn [_] (rf/dispatch [::events/mark-undone (:id todo)]))}]
          [editable-todo (:id todo)]])])))

(defn todos-panel [listid]
  [:div.list-items
   [remaining-todos-panel listid]
   [:hr]
   [completed-todos-panel listid]])

(defn add-todo-list []
  (let [name (reagent/atom "")]
    (fn []
      [:div.add-todo-list.add-object
       [:input {:type        "text"
                :placeholder "Enter list name..."
                :value       @name
                :on-change   (fn [val]
                               (reset! name (.-value (.-target val))))}]
       [:button {:type     "input"
                 :on-click (fn [args]
                             (rf/dispatch [::events/add-todo-list {:name @name}])
                             (reset! name ""))}
        [:i {:class "fas fa-plus"}]
        "Add Todo List"]])))

(defn lists-panel []
  (let [todo-lists (rf/subscribe [::subs/todo-lists])]
    (fn []
      [:div
       [add-todo-list]
       [:div.lists-panel
        (for [todo-list @todo-lists]
          ^{:key (:id todo-list)}
          [:div.list-container
           [:h4.list-title (:name todo-list)]
           [todos-panel (:id todo-list)]
           [add-todo (:id todo-list)]])]])))

(defn sign-in-panel []
  [:div.sign-in-panel
   [:a {:href     "#"
        :on-click auth/sign-in}
    [:img.sign-in-btn-img {:src "/images/btn_google_signin.png"
                           :alt "sign in with Google"}]]])

(defn sign-out-panel []
  [:div.sign-out-panel
   [:a {:href     "#"
        :on-click (fn [_] (auth/sign-out))}
    "Sign Out"]])

(defn main-panel []
  (let [auth-token (rf/subscribe [::subs/auth-token])]
    (fn []
      [:div
       [header]
       [:div.main-container
        (if-not @auth-token
          [sign-in-panel]
          [:div
           [sign-out-panel]
           [lists-panel]])]])))
