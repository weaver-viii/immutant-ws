(ns immutant-ws.server
    (:require [immutant.web :as web]
            [immutant.web.async :as async]
            [immutant.web.middleware :refer [wrap-session wrap-websocket]]
            [immutant.web.sse :as sse]            
            [ring.util.response :refer [response]]                       
            [compojure.core :refer [defroutes GET routes]]
            [compojure.route :refer [not-found resources ]]
            [cheshire.core :refer [generate-string]]
            [environ.core :refer [env]]
            (:gen-class)
            ))
            

(defn app1 [request]
  (async/as-channel request
    {:on-open 
     (fn [stream]
       (dotimes [i 10]
         (async/send! 
          stream 
          (str "メッセージを送っています " (inc i) "回目") 
          {:close? (= i 9)})
         (Thread/sleep 1000)))}
   
))



(defn app2 [request]
  (async/as-channel 
   request 
      {:on-message (fn [ch msg]
                  (async/send! ch (str "こんにちは､" msg "さん")))}))



;;wscat -c ws://localhost:8080/test


;; sse(server sent event)はサーバーからプッシュ型で送る仕組みです｡

(defn app3 [request]
  (sse/as-channel 
   request
   {:on-open 
    (fn [stream]
      (dotimes [i 10]
        (sse/send! stream (str (inc i) "番目のデータを送ります"))
        (Thread/sleep 1000))
      ;; サーバー側からクローズする       
      (sse/send! stream {:event "close", :データ "おしまい"}) 
      )}   
   ))


(defn app4 [request]
  (sse/as-channel 
   request
   {:on-open 
    (fn [stream]
      (dotimes [i 1000]
        (sse/send! 
         stream 
         (str (inc i) ":" (generate-string {:tag-id 1 :date (java.util.Date.) :value (format "%2.3f" (rand 100))})))
        (Thread/sleep 1000))
      ;; サーバー側からクローズする       
      (sse/send! stream {:event "close", :データ "おしまい"}) 
      )}   
   ))

(defroutes ws-routes
  (GET "/app1" [] app1)
  (GET "/app2" [] app2)
  (GET "/app3" [] app3)
  (GET "/app4" [] app4)
  (not-found "<p>404 指定されたページはありません.</p>")
  )

(defn -main [& {:as args}]
  (println "WebSocketのサーバーを起動します") 
  (web/run ws-routes
    (merge {"host" (env :host), "port" (env :port)}
           args)
    
    )
  ;;(web/run ws-routes {:host "localhost" :port 8080 :path "/test" })
  )

(defn app0 [request]
  {:status 200
   :body "Hello immutant wildfly server !"})

(def callbacks
      {:on-message (fn [ch msg]
                 (async/send! ch (str "こんにちは､" msg "さん")))})

(defn app5 [request]
  (if (:websocket? request)
    (async/as-channel request callbacks)
    (do
      (println request)
     (-> request
        (get-in [:params "msg"])
        .toUpperCase
        ;;(#(str "hello､" % "!"))
        ring.util.response/response)
     )     
    )  
  )






