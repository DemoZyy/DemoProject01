package com.pubnub.api;

import org.json.JSONArray;
import static com.pubnub.api.PubnubError.*;
import org.json.JSONException;

public abstract class HistoryCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(HistoryResult result);
    
    @Override
    void successCallback(String channel, Object message, Result result) {
        HistoryResult hresult = (HistoryResult)result;
        try {
            hresult.data.messages = ((JSONArray) message).getJSONArray(0);
            hresult.data.start = (String)((JSONArray) message).getString(1);
            hresult.data.end = (String)((JSONArray) message).getString(2);

            result(hresult);
        } catch (JSONException e) {
            // ERROR
            e.printStackTrace();
        } catch(Exception e) {
            // ERROR
            e.printStackTrace();
        }
        
    }
    
    @Override
    void errorCallback(String channel, PubnubError error, Result result) {
        
        ErrorStatus status = fillErrorStatusDetails(error, result);
        status.operation = OperationType.HISTORY;
        status.errorData.channels = new String[]{channel};
        status(status);

    }
}
