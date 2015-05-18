


(defn test-pubnub []
   (.subscribe (new com.pubnub.api.Pubnub "demo" "demo") "a" (proxy [com.pubnub.api.Callback] [] 
   		(successCallback ([channel message] (println (str message " " channel)))
   		([channel message timetoken] ))
   		(connectCallback [channel message] (println (str "Connected to " channel)))
   		)))
(test-pubnub)

