package com.pubnub.api;

public interface PubnubPamAsyncGrantApiStateCGOptions extends PubnubPamAsyncGrantEnd {
    PubnubPamAsyncGrantApiStateCGOptions read(boolean read);
    PubnubPamAsyncGrantApiStateCGOptions manage(boolean manage);
    PubnubPamAsyncGrantApiStateCGOptions ttl(int ttl);
    PubnubPamAsyncGrantApiStateCGOptions authKey(String authKey);
}
