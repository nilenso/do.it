(ns doit-web.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [re-frame-datatable.core :as rdt]
            [doit-web.subs :as subs]))

(defn element-value [event]
  (-> event .-target .-value))

(defn wrap-event-value
  [on-change-fn]
  (fn [event]
    (on-change-fn (element-value event))))

(defn input
  [{:keys [on-change] :as attrs}]
  [:input (assoc attrs
                 :on-change (wrap-event-value on-change))])

(defn add-todo-form []
  (let [body (reagent/atom "")
        submit-fn (fn [value]
                 (rf/dispatch [:add-todo {:body value}])
                 (reset! body ""))
        clear-fn (fn []
                (reset! body ""))]
    (fn []
      [:div
       [:label "What do you want to get done today?"]
       [:br]
       [input {:type "text"
               :name "body"
               :value @body
               :on-change #(reset! body %)}]
       [:button.btn.primary
        {:type "input" :on-click #(submit-fn @body)}
        "Add Todo"]
       [:button.btn.primary
        {:type "input" :on-click clear-fn}
        "Clear"]])))

(defn list-todos []
  [rdt/datatable
   :todos
   [::subs/todos]
   [{::rdt/column-key [:body]}]])

(defn main-panel []
  [:div
   [:h1 "DO.IT"]
   [:div.panel
    [add-todo-form]
    [:br]
    [list-todos]]])
