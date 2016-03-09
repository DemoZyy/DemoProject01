package com.pubnub.api;

public interface PubnubPamRevokeApiStateChannel {
    PubnubPamAsyncRevokeApiStateCOptions channel(String channel);
    PubnubPamAsyncRevokeApiStateCGOptions channelGroup(String channelGroup);
}
