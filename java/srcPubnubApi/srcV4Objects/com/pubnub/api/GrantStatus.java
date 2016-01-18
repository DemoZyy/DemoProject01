package com.pubnub.api;

public class GrantStatus extends Result implements StatusInterface {
	GrantData data;
	Status status;
	
	public GrantStatus(){
		status = new Status();
	}
	
	
	public GrantData getData() {
		return data;
	}

	void setData(GrantData data) {
		this.data = data;
	}

	public String toString() {
		String s = super.toString();
		s = s + status + "\n";
		return s;
		
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
