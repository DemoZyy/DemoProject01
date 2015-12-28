package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONObject;

public interface PubnubPublishApiState1 extends PubnubPublishEnd {
    PubnubPublishApiState2 message(String message);
    PubnubPublishApiState2 message(Double message);
    PubnubPublishApiState2 message(Integer message);
    PubnubPublishApiState2 message(JSONArray message);
    PubnubPublishApiState2 message(JSONObject message);
}
