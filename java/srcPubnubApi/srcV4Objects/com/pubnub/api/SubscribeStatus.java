package com.pubnub.api;

public class SubscribeStatus extends SubscribeResult implements StatusInterface {

	Status status;
	
	public SubscribeStatus(){
		status = new Status();
	}
	
	public SubscribeStatus(SubscribeResult result) {
		this();
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
	
	@Override
	public StatusCategory getCategory() {
		return status.category;
	}

	@Override
	public boolean isError() {
		return status.isError;
	}

	@Override
	public boolean wasAutoRetried() {
		return status.wasAutoRetried;
	}

	@Override
	public void retry() {

	}
	
	public String toString() {
		String s = super.toString();
		s = s + status + "\n";
		return s;	
	}

	
}
