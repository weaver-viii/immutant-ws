(ns immutant-ws.server
  (:require [immutant.web :as web]
            [immutant.web.async :as async]
            [immutant.web.middleware :refer [wrap-session wrap-websocket]]
            [immutant.web.sse :as sse]            
            [ring.util.response :refer [response]]                       
            [compojure.core :refer [defroutes GET routes]]
            [compojure.route :refer [not-found resources ]]
            
))

(defn app [request]
    {:status 200
   :body "Hello immutant wildfly server !"})

(defn app1 [request]
  (async/as-channel request
    {:on-open (fn [stream]
                (dotimes [msg 10]
                  (async/send! stream (str "メッセージを送っています " (inc msg) "回目") 
                               {:close? (= msg 9)})
                  (Thread/sleep 1000)))}))


(def callbacks
  {:on-message (fn [ch msg]
                 (async/send! ch (str "こんにちは､" msg "さん")))})

(defn app2 [request]
  (async/as-channel request callbacks))


;;wscat -c ws://localhost:8080/test


(defn app3 [request]
  (if (:websocket? request)
    (async/as-channel request callbacks)
     (-> request
        (get-in [:query-string "msg"])
        (#(str "こんにちは､" % "さん"))
        ring.util.response/response)
    )  
  )

(defn app4 [request]
  (sse/as-channel request
    {:on-open (fn [stream]
                (dotimes [e 10]
                  (sse/send! stream e)
                  (Thread/sleep 1000))
                (sse/send! stream {:event "close", :データ "bye!"}))}))

(defroutes ws-routes
  (GET "/app1" [] app1)
  (GET "/app2" [] app2)
  (GET "/app3" [] app3)
  (GET "/app4" [] app4)
  (not-found "<p>404 指定されたページはありません.</p>")
  )


(defn -main [& {:as args}]
  (println "WebSocketのサーバーを起動します")  
  (web/run-dmc ws-routes {:host "localhost" :port 8080 :path "/test"})
  )

;;(web/run ws-routes {:host "localhost" :port 8080 :path "/test"})
;;(web/run app1 {:host "localhost" :port 8080 :path "/test"})
;;(web/run app2 {:host "localhost" :port 8080 :path "/test"})
;;(web/run app3 {:host "localhost" :port 8080 :path "/test"})
;;(web/run app4 {:host "localhost" :port 8080 :path "/test"})
;;(web/run app5 {:host "localhost" :port 8080 :path "/test"})

;;(web/stop :host "localhost" :port 8080 :path "/test")





