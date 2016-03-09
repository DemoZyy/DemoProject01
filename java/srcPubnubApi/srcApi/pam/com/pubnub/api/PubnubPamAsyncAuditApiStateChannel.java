package com.pubnub.api;

public interface PubnubPamAsyncAuditApiStateChannel extends PubnubPamAsyncAuditEnd {
    PubnubPamAsyncAuditApiStateCOptions channel(String channel);
    PubnubPamAsyncAuditApiStateCGOptions channelGroup(String channelGroup);
}
