package com.pubnub.api;

public class SubscribeData {
	Object message;
	String timetoken;
	public String toString() {

		String s = "";
		s = s + "Timetoken: " + timetoken + "\n";
		s = s + "Message: " + message + "\n";

		return s;
	}
}
