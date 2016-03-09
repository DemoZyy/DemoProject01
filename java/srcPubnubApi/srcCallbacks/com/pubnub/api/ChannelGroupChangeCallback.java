package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ChannelGroupChangeCallback extends Callback {
    public abstract void status(AcknowledgmentStatus status);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        AcknowledgmentStatus status = new AcknowledgmentStatus(result);
        status(status);
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        AcknowledgmentStatus status = new AcknowledgmentStatus(fillErrorStatusDetails(error, result));

        // operation type to be filled by API
        
        status.errorData.channels = new String[]{channel};
        status(status);     
    }
    
}
