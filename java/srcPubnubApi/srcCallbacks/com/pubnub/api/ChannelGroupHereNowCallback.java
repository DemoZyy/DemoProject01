package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ChannelGroupHereNowCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(ChannelGroupHereNowResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        ChannelGroupHereNowResult hresult = (ChannelGroupHereNowResult)result;
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

            hresult.data.uuids = ChannelGroupHereNowData.getUuidDataArray(((JSONObject) message).getJSONArray("uuids"));
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
        status.operation = OperationType.HERE_NOW_FOR_CHANNEL_GROUP;
        status.errorData.channels = new String[]{channel};
        status(status);  
    }
}
