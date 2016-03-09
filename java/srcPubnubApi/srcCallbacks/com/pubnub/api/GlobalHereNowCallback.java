package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class GlobalHereNowCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(GlobalHereNowResult result);
    
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        GlobalHereNowResult hresult = (GlobalHereNowResult)result;
        System.out.println(message);
        try {
            hresult.data.occupancy = ((JSONObject) message).getInt("occupancy");
        } catch (JSONException e) {
            // ERROR
            //e.printStackTrace();
        } catch(Exception e) {
            //e.printStackTrace();
        }
        try {

            hresult.data.uuids = GlobalHereNowData.getUuidDataArray(((JSONObject) message).getJSONArray("uuids"));
        } catch (JSONException e) {
            // ERROR
            //e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        result(hresult);
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.HERE_NOW_GLOBAL;
        status.errorData.channels = new String[]{channel};
        status(status);  
    }
}
