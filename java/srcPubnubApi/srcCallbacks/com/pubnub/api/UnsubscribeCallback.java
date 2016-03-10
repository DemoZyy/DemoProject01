package com.pubnub.api;

public abstract class UnsubscribeCallback extends Callback {
    public abstract void status(AcknowledgmentStatus status);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        /*
        ClientStateUpdateStatus status = new ClientStateUpdateStatus();
        status = (ClientStateUpdateStatus) fillErrorStatusDetails(status, error, result);
        status.operation = OperationType.SET_STATE;
        status.errorData.channels = new String[]{channel};
        status(status);  
        */
    }
}
