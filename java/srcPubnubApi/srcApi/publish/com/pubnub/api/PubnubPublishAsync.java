package com.pubnub.api;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

public class PubnubPublishAsync implements PubnubPublishAsyncInterface {

    
    private Pubnub pubnub;
    private PublishCallback callback;
    private String channel;
    private Object message;
    private JSONObject metadata;
    PubnubPublishAsync pns = this;
    private boolean storeInHistory;
    
    PubnubPublishAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void _send() {
        Hashtable args = new Hashtable();

        PubnubUtil.addToHash(args, "channel", channel);
        PubnubUtil.addToHash(args, "message", message);
        PubnubUtil.addToHash(args, "storeInHistory", (storeInHistory) ? "" : "0");
        PubnubUtil.addToHash(args, "meta", metadata);
        PubnubUtil.addToHash(args, "callback", callback);
    
        if (pubnub != null) pubnub._publish(args, false);
    }
    
    PubnubPublishAsyncApiStateMessage asyncApiStateMessage = new PubnubPublishAsyncApiStateMessage(){

        @Override
        public void send() {
            _send();
        }

        @Override
        public PubnubPublishAsyncApiStateOptions message(String message) {
            pns.message = message;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubPublishAsyncApiStateOptions message(Double message) {
            pns.message = message;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubPublishAsyncApiStateOptions message(Integer message) {
            pns.message = message;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubPublishAsyncApiStateOptions message(JSONArray message) {
            pns.message = message;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubPublishAsyncApiStateOptions message(JSONObject message) {
            pns.message = message;
            return asyncApiStateOptions;
        }
    };
    
    PubnubPublishAsyncApiStateOptions asyncApiStateOptions = new PubnubPublishAsyncApiStateOptions(){

        @Override
        public void send() {
            _send();
        }

        @Override
        public PubnubPublishAsyncApiStateOptions storeInHistory(boolean storeInHistory) {
            pns.storeInHistory = storeInHistory;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubPublishAsyncApiStateOptions metadata(JSONObject metadata) {
            pns.metadata = metadata;
            return asyncApiStateOptions;
        }
        
    };


    
    
    PubnubPublishAsyncApiStateChannel asyncApiStateChannel = new PubnubPublishAsyncApiStateChannel(){

        @Override
        public PubnubPublishAsyncApiStateMessage channel(String channel) {
            pns.callback = callback;
            return asyncApiStateMessage;
        }
        
    };
    
    @Override
    public PubnubPublishAsyncApiStateChannel callback(PublishCallback callback) {
        pns.callback = callback;
        return asyncApiStateChannel;
    }

}
