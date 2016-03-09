package com.pubnub.api;

import org.json.JSONObject;

public interface PubnubPublishAsyncApiStateOptions extends PubnubPublishAsyncEnd {
    PubnubPublishAsyncApiStateOptions storeInHistory(boolean storeInHistory);
    PubnubPublishAsyncApiStateOptions metadata(JSONObject metadata);
}

