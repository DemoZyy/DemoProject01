package com.pubnub.api;

import org.json.JSONObject;

public class PubnubHereNowAsync implements PubnubHereNowAsyncInterface {
    private Pubnub pubnub;
    private HereNowCallback callback;
    private String channel;
    private String channelGroup;
    private boolean state = false;
    private boolean uuids = false;

    PubnubHereNowAsync pns = this;

    
    PubnubHereNowAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void _get() {
    
        if (pubnub != null) {
            if (channel != null) {
                pubnub.hereNow(new String[]{channel}, null, state, uuids, callback);
            } else if (channelGroup != null) {
                pubnub.hereNow(null, new String[]{channelGroup}, state, uuids, callback);                
            }
        }
        
    }

    
    PubnubHereNowAsyncApiStateOptions asyncApiStateOptions = new PubnubHereNowAsyncApiStateOptions(){

        @Override
        public void get() {
            _get();
        }

        @Override
        public PubnubHereNowAsyncApiStateOptions uuids(boolean uuids) {
            pns.uuids = uuids;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubHereNowAsyncApiStateOptions state(boolean state) {
            pns.state = state;
            return asyncApiStateOptions;
        }

    };


    
    
    PubnubHereNowAsyncApiStateChannel asyncApiStateChannel = new PubnubHereNowAsyncApiStateChannel(){

        @Override
        public PubnubHereNowAsyncApiStateOptions channel(String channel) {
            pns.channel = channel;
            return asyncApiStateOptions;
        }

        @Override
        public PubnubHereNowAsyncApiStateOptions channelGroup(String channelGroup) {
            pns.channelGroup = channelGroup;
            return asyncApiStateOptions;
        }
        
    };
    
    @Override
    public PubnubHereNowAsyncApiStateChannel callback(HereNowCallback callback) {
        pns.callback = callback;
        return asyncApiStateChannel;
    }

}
