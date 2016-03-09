package com.pubnub.api;

import com.pubnub.api.ErrorStatus;
import com.pubnub.api.PubnubError;
import com.pubnub.api.Result;

public abstract class PamModifyCallback extends Callback {
    public abstract void status(AcknowledgmentStatus status);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
    }
}
