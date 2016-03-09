package com.pubnub.api;

public interface PubnubHistorySyncApiStateOptions extends PubnubHistorySyncEnd {
    PubnubHistorySyncApiStateOptions count(int count);
    PubnubHistorySyncApiStateOptions start(long start);
    PubnubHistorySyncApiStateOptions end(long start);
    PubnubHistorySyncApiStateOptions reverse(boolean reverse);
    PubnubHistorySyncApiStateOptions includeToken(boolean includeToken);
}
