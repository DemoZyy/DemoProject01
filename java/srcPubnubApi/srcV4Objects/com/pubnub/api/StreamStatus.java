package com.pubnub.api;

public class StreamStatus extends StreamResult implements StatusInterface  {

	boolean wasAutoRetried;
	boolean isError;
	StatusCategory category;
    
	public StreamStatus(StreamResult result) {
		this();
		this.clientRequest = result.clientRequest;
		this.code = result.code;
		this.config = result.config;
		this.connectionId = result.connectionId;
		this.hreq = result.hreq;
		this.operation = result.operation;
		this.pubnub = result.pubnub;
		this.serverResponse = result.serverResponse;
		this.type = ResultType.STATUS;
	    data = new StreamData();
	    data.message = result.data.message;
	    data.timetoken = result.data.timetoken;
	}
	
	public StreamStatus() {
        // TODO Auto-generated constructor stub
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

	@Override
	public void retry() {

	}
	
	public String toString() {
		String s = super.toString();
		s = s + "Was Auto Retried ? : " + wasAutoRetried + "\n";
		s = s + "Is Error ? : " + isError + "\n";
		s = s + "Category : " + category + "\n";
		return s;	
	}

}
