package com.pubnub.api;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

public class PubnubHistoryAsync implements PubnubHistoryAsyncInterface {
    
    private Pubnub pubnub;
    private HistoryCallback callback;
    private String channel;
    private long start = -1;
    private long end = -1;
    private boolean reverse = false;
    private int count = -1;
    private boolean includeTimetoken = false;
    private Object message;
    private JSONObject metadata;
    PubnubHistoryAsync pns = this;
    private boolean storeInHistory;
    
    PubnubHistoryAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void _get() {
    
        if (pubnub != null) pubnub._history(channel, start, end, count, reverse, includeTimetoken, callback, false);
        
    }

    
    PubnubHistoryAsyncApiStateOptions asyncApiStateOptions = new PubnubHistoryAsyncApiStateOptions(){

        @Override
        public void get() {
            _get();
        }

        @Override
        public PubnubHistoryAsyncApiStateOptions count(int count) {
            pns.count = count;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubHistoryAsyncApiStateOptions start(long start) {
            pns.start = start;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubHistoryAsyncApiStateOptions end(long end) {
            pns.end = end;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubHistoryAsyncApiStateOptions reverse(boolean reverse) {
            pns.reverse = reverse;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubHistoryAsyncApiStateOptions includeToken(boolean includeToken) {
            pns.includeTimetoken = includeToken;
            return asyncApiStateOptions;
        }


    };


    
    
    PubnubHistoryAsyncApiStateChannel asyncApiStateChannel = new PubnubHistoryAsyncApiStateChannel(){

        @Override
        public PubnubHistoryAsyncApiStateOptions channel(String channel) {
            pns.channel = channel;
            return asyncApiStateOptions;
        }
        
    };
    
    @Override
    public PubnubHistoryAsyncApiStateChannel callback(HistoryCallback callback) {
        pns.callback = callback;
        return asyncApiStateChannel;
    }

}
