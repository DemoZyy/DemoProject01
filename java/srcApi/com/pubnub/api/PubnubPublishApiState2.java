package com.pubnub.api;

import org.json.JSONObject;

public interface PubnubPublishApiState2 extends PubnubPublishEnd {
    PubnubPublishApiState3 storeInHistory(boolean storeInHistory);
    PubnubPublishApiState4 metadata(JSONObject metadata);
}
