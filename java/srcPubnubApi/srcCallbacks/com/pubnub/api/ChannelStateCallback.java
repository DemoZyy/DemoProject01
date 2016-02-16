package com.pubnub.api;

public abstract class ChannelStateCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(ChannelClientStateResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
    }
}
