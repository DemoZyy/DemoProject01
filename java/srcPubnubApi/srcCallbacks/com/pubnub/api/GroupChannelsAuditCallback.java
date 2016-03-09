package com.pubnub.api;

public abstract class GroupChannelsAuditCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(ChannelGroupChannelsResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.CHANNELS_FOR_GROUP;
        status.errorData.channels = new String[]{channel};
        status(status);          
    }
}
