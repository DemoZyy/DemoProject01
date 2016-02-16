package com.pubnub.api;

public class PublishStatus extends AcknowledgmentStatus {
	PublishData data;
	
	public PublishData getData() {
		return data;
	}

	void fillPublishStatusDetails() {
        category = category.ACK;
        operation = operation.PUBLISH; 
	}
	
	public PublishStatus(Result result) {
	    super(result);
		data = new PublishData();
		fillPublishStatusDetails();

	}
	
	public PublishStatus() {
        super();
        fillPublishStatusDetails();
    }

    public String toString() {
		String s = super.toString();
		s = s + data + "\n";
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

	@Override
	public void retry() {
		pubnub.sendNonSubscribeRequest(hreq);
	}
}
