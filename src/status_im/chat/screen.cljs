(ns status-im.chat.screen
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame]
            [status-im.chat.styles.screen :as style]
            [status-im.utils.platform :as platform]
            [status-im.chat.views.toolbar-content :as toolbar-content]
            [status-im.chat.views.message.message :as message]
            [status-im.chat.views.message.datemark :as message-datemark]
            [status-im.chat.views.input.input :as input]
            [status-im.chat.views.actions :as actions]
            [status-im.chat.views.bottom-info :as bottom-info]
            [status-im.i18n :as i18n]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.list.views :as list]
            [status-im.ui.components.list-selection :as list-selection]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.status-bar.view :as status-bar]
            [status-im.ui.components.chat-icon.screen :as chat-icon-screen]
            [status-im.ui.components.sync-state.offline :as offline]
            [status-im.ui.components.toolbar.view :as toolbar]
            [status-im.ui.components.animation :as animation]))

(defview chat-icon []
  (letsubs [{:keys [chat-id group-chat name]} [:get-current-chat]]
    [chat-icon-screen/chat-icon-view-action chat-id group-chat name true]))

(defn toolbar-action [chat-id chat-name group-chat public?]
  [react/touchable-highlight
   {:on-press            #(list-selection/show {:title chat-name
                                                :options (actions/chat-actions chat-id group-chat public?)})
    :accessibility-label :chat-menu}
   [react/view style/action
    [vector-icons/icon :icons/dots-horizontal]]])

(defview add-contact-bar []
  (letsubs [chat-id          [:get-current-chat-id]
            pending-contact? [:current-contact :pending?]]
    (when pending-contact?
      [react/touchable-highlight
       {:on-press #(re-frame/dispatch [:add-pending-contact chat-id])}
       [react/view style/add-contact
        [react/text {:style style/add-contact-text}
         (i18n/label :t/add-to-contacts)]]])))

(defview chat-toolbar []
  (letsubs [chat-id       [:get-current-chat-id]
            accounts      [:get-accounts]
            creating?     [:get :accounts/creating-account?]
            chat-name     [:chat :name]
            group-chat    [:chat :group-chat]
            public?       [:chat :public?]]
    [react/view
     [status-bar/status-bar]
     [toolbar/platform-agnostic-toolbar {}
      (when-not creating?
        (if (empty? accounts)
          [toolbar/nav-clear-text (i18n/label :t/recover)
           #(re-frame/dispatch [:navigate-to-modal :recover-modal])]
          toolbar/default-nav-back))
      [toolbar-content/toolbar-content-view]
      [toolbar-action chat-id chat-name group-chat public?]]
     [add-contact-bar]]))

(defmulti message-row (fn [{{:keys [type]} :row}] type))

(defmethod message-row :datemark
  [{{:keys [value]} :row}]
  [message-datemark/chat-datemark value])

(defmethod message-row :default
  [{:keys [group-chat current-public-key row]}]
  [message/chat-message (assoc row
                               :group-chat group-chat
                               :current-public-key current-public-key)])


(defview messages-view-animation [message-view]
  ;; smooths out appearance of message-view
  (letsubs [opacity       (animation/create-value 0)
            duration      (if platform/android? 100 200)
            timeout       (if platform/android? 50 0)]
    {:component-did-mount (fn [_]
                            (animation/start
                             (animation/anim-sequence
                              [(animation/anim-delay timeout)
                               (animation/spring opacity {:toValue  1
                                                          :duration duration})])))}
    [react/with-activity-indicator
     {:style   style/message-view-preview
      :preview [react/view style/message-view-preview]}
     [react/animated-view {:style (style/message-view-animated opacity)}
      message-view]]))

(defview messages-view [group-chat]
  (letsubs [messages           [:get-current-chat-messages]
            current-public-key [:get-current-public-key]]
    [list/flat-list {:data                      messages
                     :render-fn                 (fn [{:keys [message-id] :as message}]
                                                  ^{:key message-id}
                                                  [message-row {:group-chat         group-chat
                                                                :current-public-key current-public-key
                                                                :row                message}])
                     :inverted                  true
                     :onEndReached              #(re-frame/dispatch [:load-more-messages])
                     :enableEmptySections       true
                     :keyboardShouldPersistTaps (if platform/android? :always :handled)}]))

(defview chat []
  (letsubs [{:keys [group-chat input-text]} [:get-current-chat]
            show-bottom-info?               [:get-current-chat-ui-prop :show-bottom-info?]
            layout-height                   [:get :layout-height]
            current-view                    [:get :view-id]]
    {:component-will-unmount #(re-frame/dispatch [:set-chat-ui-props {:show-emoji? false}])}
    [react/view {:style style/chat-view
                 :on-layout (fn [event]
                              (let [height (.. event -nativeEvent -layout -height)]
                                (when (not= height layout-height)
                                  (re-frame/dispatch [:set-layout-height height]))))}
     [chat-toolbar]
     (when (= :chat current-view)
       [messages-view-animation
        [messages-view group-chat]])
     [input/container {:text-empty? (string/blank? input-text)}]
     (when show-bottom-info?
       [bottom-info/bottom-info-view])
     [offline/offline-view {:top (get platform/platform-specific :status-bar-default-height)}]]))
