package com.pubnub.api;

public abstract class ChannelGroupChangeCallback extends Callback {
    public abstract void status(AcknowledgmentStatus status);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
    }
    
}
