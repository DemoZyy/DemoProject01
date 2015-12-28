package com.pubnub.api;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

public class PubnubPublish implements PubnubPublishInterface {

    
    private Pubnub pubnub;
    private Callback callback;
    private String channel;
    private Object message;
    private JSONObject metadata;
    PubnubPublish pns = this;
    private boolean storeInHistory;
    
    PubnubPublish(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void _send() {
        Hashtable args = new Hashtable();

        PubnubUtil.addToHash(args, "channel", channel);
        PubnubUtil.addToHash(args, "message", message);
        PubnubUtil.addToHash(args, "storeInHistory", (storeInHistory) ? "" : "0");
        PubnubUtil.addToHash(args, "meta", metadata);
    
        if (pubnub != null) pubnub.publish(args, callback);
    }
    
    PubnubPublishApiState1 apiState1 = new PubnubPublishApiState1(){

        @Override
        public void send() {
            _send();
        }

        @Override
        public PubnubPublishApiState2 message(String message) {
            pns.message = message;
            return apiState2;
        }

        @Override
        public PubnubPublishApiState2 message(Double message) {
            pns.message = message;
            return apiState2;
        }

        @Override
        public PubnubPublishApiState2 message(Integer message) {
            pns.message = message;
            return apiState2;
        }

        @Override
        public PubnubPublishApiState2 message(JSONArray message) {
            pns.message = message;
            return apiState2;
        }

        @Override
        public PubnubPublishApiState2 message(JSONObject message) {
            pns.message = message;
            return apiState2;
        }
    };
    
    PubnubPublishApiState2 apiState2 = new PubnubPublishApiState2(){

        @Override
        public void send() {
            _send();
        }

        @Override
        public PubnubPublishApiState3 storeInHistory(boolean storeInHistory) {
            pns.storeInHistory = storeInHistory;
            return apiState3;
        }

        @Override
        public PubnubPublishApiState4 metadata(JSONObject metadata) {
            pns.metadata = metadata;
            return apiState4;
        }
        
    };
    PubnubPublishApiState3 apiState3 = new PubnubPublishApiState3(){

        @Override
        public void send() {
            _send();
        }

        @Override
        public PubnubPublishApiState4 metadata(JSONObject metadata) {
            pns.metadata = metadata;
            return apiState4;
        }
    };
    
    PubnubPublishApiState4 apiState4 = new PubnubPublishApiState4(){

        @Override
        public void send() {
            _send();
        }

        @Override
        public PubnubPublishApiState3 storeInHistory(boolean storeInHistory) {
            pns.storeInHistory = storeInHistory;
            return apiState3;
        }
    };
    
    PubnubPublishEnd apiEnd = new PubnubPublishEnd(){

        @Override
        public void send() {
            _send();
        }
        
    };
    
    
    PubnubPublishApiState apiState = new PubnubPublishApiState(){

        @Override
        public PubnubPublishApiState1 channel(String channel) {
            pns.channel = channel;
            return apiState1;
        }
        
    };
    
    @Override
    public PubnubPublishApiState callback(Callback callback) {
        pns.callback = callback;
        return apiState;
    }

}
