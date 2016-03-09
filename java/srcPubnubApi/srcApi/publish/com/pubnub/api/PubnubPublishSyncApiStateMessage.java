package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONObject;

public interface PubnubPublishSyncApiStateMessage extends PubnubPublishSyncEnd {
    PubnubPublishSyncApiStateOptions message(String message);
    PubnubPublishSyncApiStateOptions message(Double message);
    PubnubPublishSyncApiStateOptions message(Integer message);
    PubnubPublishSyncApiStateOptions message(JSONArray message);
    PubnubPublishSyncApiStateOptions message(JSONObject message);
}
