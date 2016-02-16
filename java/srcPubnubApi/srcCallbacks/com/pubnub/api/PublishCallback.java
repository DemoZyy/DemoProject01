package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;

public abstract class PublishCallback extends Callback {
    public abstract void status(PublishStatus status);
    
    @Override
    void successCallback(String channel, Object message, Result result) {

        PublishStatus status = new PublishStatus(result);
        status.isError = false;
        try {
            status.data.timetoken = ((JSONArray) message).getString(2);
            status.data.information = ((JSONArray) message).getString(1);
            status(status);
        } catch (JSONException e) {
            e.printStackTrace();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        PublishStatus status = new PublishStatus(result);
        status.errorData.channels = new String[]{channel};
        status(status);
    }
}
