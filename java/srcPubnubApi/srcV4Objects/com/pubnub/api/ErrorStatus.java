package com.pubnub.api;

public class ErrorStatus extends Status {

    ErrorData errorData = new ErrorData();
    
    @Override
    public void retry() {
        pubnub.sendNonSubscribeRequest(hreq);
    }

    public ErrorData getErrorData() {
        return errorData;
    }
    
    ErrorStatus() {
        super();
        this.type = ResultType.STATUS;
    }

    ErrorStatus(Result result) {
        super(result);
        errorData = new ErrorData();
        this.isError = true;
        this.code = result.code;
        this.operation = result.operation;
        this.config = result.config;
        this.connectionId = result.connectionId;
        this.clientRequest = result.clientRequest;
        this.serverResponse = result.serverResponse;
        this.hreq = result.hreq;
        this.pubnub = result.pubnub;
        this.type = ResultType.STATUS;
    }
    
    ErrorStatus(Status status) {
        super();
        this.wasAutoRetried = status.wasAutoRetried;
        this.isError = true;
        this.category = status.category;
        errorData = new ErrorData();
    }
    
    public String toString()    {
        String s = "";
        s += super.toString() + "\n";
        s += errorData.toString() + "\n";
        return s;
    }

}
