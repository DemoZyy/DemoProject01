package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONObject;

public interface PubnubPublishAsyncApiStateMessage extends PubnubPublishAsyncEnd {
    PubnubPublishAsyncApiStateOptions message(String message);
    PubnubPublishAsyncApiStateOptions message(Double message);
    PubnubPublishAsyncApiStateOptions message(Integer message);
    PubnubPublishAsyncApiStateOptions message(JSONArray message);
    PubnubPublishAsyncApiStateOptions message(JSONObject message);
}
