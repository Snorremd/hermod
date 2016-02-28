(ns leiningen.hermod
  "Main hermod plugin namespace activates lein hooks for lein deploy and
  contains all logic needed to send notifications to Hipchat and Slack."
  (:require
    [robert.hooke]
    [leiningen.deploy]
    [clojure.pprint :refer [pprint]]
    [clj-http.client :as client]
    [clojure.data.json :as json]))

(defn http-request
  "Performs https request against the specified url with the provided payload.
  url should be a string and payload a standard edn/clojure map."
  [url payload service]
  (try (client/post url {:content-type :json
                         :body (json/write-str payload)})
       (catch
         Exception
         e
         (println "Failed to deliver notification to " service)
         e)))

(defn send-to-slack
  "Send message to slack room via Slack HTTP API.
   Source (gist): https://gist.github.com/mikebroberts/9604828"
  [url channel botname message]
  (http-request url
                (if-not channel
                  {:text message
                   :username botname}
                  (assoc {:text message
                          :username botname}
                    :channel channel))
                "Slack"))

(defn hipchat-notification-url
  "Build a hipchat api notification url with room and token parameter."
  [room token]
  (str "https://api.hipchat.com/v2/room/"
       room
       "/notification?auth_token="
       token))

(defn send-to-hipchat
  "Send notification to Hipchat room via Hipchat HTTP API."
  [token room botname message]
  (pprint (http-request (hipchat-notification-url room token)
                        {:message message
                         :from    botname}
                        "Hipchat")))

(defn deploy-hook
  "Send notifications to Hipchat and/or Slack when lein deploy is run."
  [task & args]
  (let [result (apply task args)
        message (str "Deployed "
                     (-> args first :name)
                     ":"
                     (-> args first :version))
        botname (or (-> args first :hermod :botname) "Hermóðr")]
    (if-let [hipchat (-> args first :hermod :hipchat)]
      (send-to-hipchat (:token hipchat) (:room hipchat) botname message))
    (if-let [slack (-> args first :hermod :slack)]
      (send-to-slack (:webhook slack) (:channel slack) botname message))
    result))

(defn activate []
  "active lein hook."
  (robert.hooke/add-hook #'leiningen.deploy/deploy #'deploy-hook))
