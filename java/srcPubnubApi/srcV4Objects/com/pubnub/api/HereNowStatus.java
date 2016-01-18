package com.pubnub.api;

public class HereNowStatus extends HereNowResult implements StatusInterface {
	Status status;
	
	public String toString() {
		String s = super.toString();
		s = s + status + "\n";
		return s;
		
	}

	public HereNowStatus(HereNowResult result) {
		this.clientRequest = result.clientRequest;
		this.operation = result.operation;
		this.code = result.code;
		this.config = result.config;
		this.connectionId = result.connectionId;
		this.serverResponse = result.serverResponse;
		this.data = result.data;
		this.hreq = result.hreq;
		this.pubnub = result.pubnub;
		status = new Status();
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
		pubnub.sendNonSubscribeRequest(hreq);
	}
}
