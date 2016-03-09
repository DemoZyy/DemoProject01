package com.pubnub.api;

public interface PubnubPamAsyncRevokeApiStateChannel extends PubnubPamAsyncRevokeEnd {
    PubnubPamAsyncRevokeApiStateOptions channel(String channel);
    PubnubPamAsyncRevokeApiStateOptions channelGroup(String channelGroup);
}
