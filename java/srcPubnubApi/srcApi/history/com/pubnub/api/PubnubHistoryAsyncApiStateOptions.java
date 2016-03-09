package com.pubnub.api;

public interface PubnubHistoryAsyncApiStateOptions extends PubnubHistoryAsyncEnd {
    PubnubHistoryAsyncApiStateOptions count(int count);
    PubnubHistoryAsyncApiStateOptions start(long start);
    PubnubHistoryAsyncApiStateOptions end(long start);
    PubnubHistoryAsyncApiStateOptions reverse(boolean reverse);
    PubnubHistoryAsyncApiStateOptions includeToken(boolean includeToken);
}
