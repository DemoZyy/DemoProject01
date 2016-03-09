package com.pubnub.api;

public interface PubnubStreamListenerInterface {
	public void streamStatus(StreamStatus status);
	public void streamResult(StreamResult result);
}
