package com.pubnub.api;

public abstract class ChannelGroupStateCallback extends Callback {
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
    }

}
