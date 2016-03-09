package com.pubnub.api;

public interface PubnubPamAsyncInterface {
    PubnubPamAsyncGrantInterface  grant();
    PubnubPamAsyncRevokeInterface revoke();
    PubnubPamAsyncAuditInterface audit();
}
