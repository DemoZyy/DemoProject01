package com.pubnub.api;

public interface PubnubHereNowSyncInterface {
    PubnubHereNowSyncApiStateOptions callback(String channel);
    PubnubHereNowSyncApiStateOptions channelGroup(String channelGroup);
}
