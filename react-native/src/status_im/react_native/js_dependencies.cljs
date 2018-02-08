(ns status-im.react-native.js-dependencies)

(def action-button          (js/require "react-native-action-button"))
(def autolink               (js/require "react-native-autolink"))
(def camera                 (js/require "react-native-camera"))
(def config                 (js/require "react-native-config"))
(def dialogs                (js/require "react-native-dialogs"))
(def dismiss-keyboard       (js/require "dismissKeyboard"))
(def emoji-picker           (js/require "react-native-emoji-picker"))
(def fs                     (js/require "react-native-fs"))
(def http-bridge            (js/require "react-native-http-bridge"))
;; i18n is now exported in default object of the module
;; https://github.com/AlexanderZaytsev/react-native-i18n/blob/master/index.js
(def i18n                   (.-default (js/require "react-native-i18n")))
(def image-crop-picker      (js/require "react-native-image-crop-picker"))
(def image-resizer          (js/require "react-native-image-resizer"))
(def instabug               (js/require "instabug-reactnative"))
(def linear-gradient        (js/require "react-native-linear-gradient"))
(def nfc                    (js/require "nfc-react-native"))
(def orientation            (js/require "react-native-orientation"))
(def qr-code                (js/require "react-native-qrcode"))
(def random-bytes           (js/require "react-native-randombytes"))
(def react-native           (js/require "react-native"))
(def realm                  (js/require "realm"))
(def sortable-listview      (js/require "react-native-sortable-listview"))
(def vector-icons           (js/require "react-native-vector-icons/Ionicons"))
(def webview-bridge         (js/require "react-native-webview-bridge"))
(def svg                    (js/require "react-native-svg"))
(def react-native-fcm       (js/require "react-native-fcm"))
(def snoopy                 (js/require "rn-snoopy"))
(def snoopy-filter          (js/require "rn-snoopy/stream/filter"))
(def snoopy-bars            (js/require "rn-snoopy/stream/bars"))
(def snoopy-buffer          (js/require "rn-snoopy/stream/buffer"))
(def EventEmmiter           (js/require "react-native/Libraries/vendor/emitter/EventEmitter"))
(def background-timer       (.-default (js/require "react-native-background-timer")))
