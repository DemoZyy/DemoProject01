package com.pubnub.api;

import com.pubnub.api.ErrorStatus;
import com.pubnub.api.PubnubError;
import com.pubnub.api.Result;

public abstract class PamAuditCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(PamAuditResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.PAM_AUDIT;
        status(status);          
    }
}
