package com.pubnub.api;

public interface PubnubHereNowSyncApiStateOptions extends PubnubHereNowSyncEnd {
    PubnubHereNowSyncApiStateOptions state(boolean state);
    PubnubHereNowSyncApiStateOptions uuids(boolean uuids);
}
