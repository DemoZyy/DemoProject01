package com.pubnub.api;

public abstract class SetStateCallback extends Callback {
    public abstract void status(ClientStateUpdateStatus status);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
    }
}
