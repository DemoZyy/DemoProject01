package com.pubnub.api;

public class TimeResult extends Result {

    TimeData data = new TimeData();
    
    TimeResult(Result result) {
        this.clientRequest = result.clientRequest;
        this.code = result.code;
        this.config = result.config;
        this.connectionId = result.connectionId;
        this.hreq = result.hreq;
        this.operation = result.operation;
        this.pubnub = result.pubnub;
        this.serverResponse = result.serverResponse;
        this.type = result.type;
    }
}
