package com.pubnub.api;

public abstract class ChannelGroupStateCallback extends Callback {
    
    public abstract void status(ErrorStatus status);
    public abstract void result(ChannelGroupClientStateResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.STATE_FOR_CHANNEL_GROUP;
        status.errorData.channels = new String[]{channel};
        status(status);   
    }

}
