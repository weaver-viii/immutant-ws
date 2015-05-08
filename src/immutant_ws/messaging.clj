(ns immutant-ws.messaging
  (:require [immutant.messaging :refer :all]
            )
  (:gen-class)
  )

(def q (queue "my-queue")) ;; キューの作成

(def listener (listen q println :concurrency 2)) ; リスナーの登録

(publish q {:hi :there}) ;; メッセージをパブリッシュ

(publish q {:hi :there} :encoding :json) ;; json形式でパブリッシュ


(stop listener) ;; リスナーの停止
;; identical to (.close listener)

(def sync-q (queue "sync")) ;; 同期キューの作成

(def responder (respond sync-q inc)) ;; responderの登録

@(request sync-q 1) ;; メッセージのリクエスト=>2が返って来る

