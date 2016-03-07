package com.pubnub.api;

public class  Status extends Result implements StatusInterface {
	boolean wasAutoRetried;
	boolean isError;
	StatusCategory category;
	
	public String toString() {
	    String s = super.toString();
		s = s + "Was Auto Retried ? : " + wasAutoRetried + "\n"; 
		s = s + "Is Error ? : " + isError + "\n";
		s = s + "Category : " + category + "\n";
		return s;
	}

    @Override
    public StatusCategory getCategory() {
        return category;
    }

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public boolean wasAutoRetried() {
        return wasAutoRetried;
    }
    Status() {
        super();
        this.type = ResultType.STATUS;
    }
    
    Status(Result result) {
        this.type = ResultType.STATUS;
        this.code = result.code;
        this.operation = result.operation;
        this.config = result.config;
        this.connectionId = result.connectionId;
        this.clientRequest = result.clientRequest;
        this.serverResponse = result.serverResponse;
    }

    @Override
    public void retry() {

    }

}
