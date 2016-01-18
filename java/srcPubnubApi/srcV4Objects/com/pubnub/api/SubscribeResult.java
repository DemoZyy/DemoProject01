package com.pubnub.api;

public class SubscribeResult extends Result {
	SubscribeData data;

	public SubscribeData getData() {
		return data;
	}
	public SubscribeResult() {
		data = new SubscribeData();
	}
	public String toString() {
		String s = super.toString();
		s = s + data + "\n";
		return s;
		
	}
}
