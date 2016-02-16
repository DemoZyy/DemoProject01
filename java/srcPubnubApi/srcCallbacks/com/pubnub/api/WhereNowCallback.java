package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class WhereNowCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(WhereNowResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        WhereNowResult hresult = (WhereNowResult)result;
        System.out.println(message);
        try {
            hresult.data.channels = PubnubUtil.jsonArrayToStringArray(((JSONObject) message).getJSONArray("channels"));
            result(hresult);
        } catch (JSONException e) {
            // ERROR
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.WHERE_NOW;
        status(status);  
    }
}
