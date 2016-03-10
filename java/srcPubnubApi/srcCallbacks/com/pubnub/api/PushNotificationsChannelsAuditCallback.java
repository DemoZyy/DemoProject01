package com.pubnub.api;

public abstract class PushNotificationsChannelsAuditCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(PushNotificationsChannelsAuditResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        //status.operation = OperationType.CHANNEL_GROUPS;
        status.errorData.channels = new String[]{channel};
        status(status);         
    }
}
