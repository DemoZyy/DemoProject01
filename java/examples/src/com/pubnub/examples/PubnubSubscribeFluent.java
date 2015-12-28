package com.pubnub.examples;

import com.pubnub.api.*;

public class PubnubSubscribeFluent {

	public static void main(String[] args) {
		//final PubnubSync pubnub = new PubnubSync("demo", "demo");
		
		//System.out.println(pubnub.publish("demo", 1));
		
		Pubnub pubnub = new Pubnub.Builder()
		        .setPublishKey("demo")
		        .setSubscribeKey("demo")
		        .build();
		
		
		
		try {
		    
		    Callback callback = new Callback(){
                public void successCallback(String channel, Object message) {
                    System.out.println(message);
                }
                public void connectCallback(String channel, Object message) {
                    System.out.println(message);
                }
                public void errorCallback(String channel, PubnubError message) {
                    System.out.println(message);
                }     
		    };
		    
		    
            pubnub.subscribe().callback(callback).channel("a").filter("foo==\"bar\"").connect();
            
            
        } catch (PubnubException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		


	}

}
