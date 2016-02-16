package com.pubnub.api;

public abstract class TimeCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(TimeResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
    }
}
