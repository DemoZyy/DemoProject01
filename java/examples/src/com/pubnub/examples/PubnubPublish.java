package com.pubnub.examples;

import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

public class PubnubPublish {

	public static void main(String[] args) {
		final Pubnub pubnub = new Pubnub("demo", "demo");
		
		Callback callback = new Callback(){
		    public void successCallback(String channel, Object message) {
		        
		    }
		    public void errorCallback(String channel, PubnubError error) {
		        
		    }
		};
		JSONObject meta = new JSONObject();
		try {
            
            meta.put("foo", "bar");
        } catch (JSONException e) {

        }
		pubnub.publish().callback(callback).channel("abcd")
		        .message("hi").storeInHistory(false).metadata(meta).send();;

	}

}
