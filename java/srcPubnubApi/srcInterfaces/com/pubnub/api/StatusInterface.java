package com.pubnub.api;

public interface StatusInterface {
	public StatusCategory getCategory();
	public boolean isError();
	public boolean wasAutoRetried();
	public void retry();
}
