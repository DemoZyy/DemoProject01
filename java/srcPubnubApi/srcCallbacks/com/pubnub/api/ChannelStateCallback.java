package com.pubnub.api;

public abstract class ChannelStateCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(ChannelClientStateResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.STATE_FOR_CHANNEL;
        status.errorData.channels = new String[]{channel};
        status(status);         
    }
}
