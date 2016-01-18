package com.pubnub.api;

public class PublishStatus extends Result implements StatusInterface {
	private PublishData data;
	
	public PublishData getData() {
		return data;
	}
	void setData(PublishData data) {
		this.data = data;
	}

	Status status;
	
	
	public PublishStatus() {
		data = new PublishData();
		status = new Status();
	}
	public String toString() {
		String s = super.toString();
		s = s + status + "\n";
		s = s + data + "\n";
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
