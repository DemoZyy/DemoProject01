package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;

public abstract class TimeCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(TimeResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
      TimeResult tresult = new TimeResult(result);
      try {
        tresult.data.timetoken = ((JSONArray) message).getString(0);
        } catch (JSONException e) {
            // Error Handler
        }
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.TIME;
        status(status);       
    }
}
