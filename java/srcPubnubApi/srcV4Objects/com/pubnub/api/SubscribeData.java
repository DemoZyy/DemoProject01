package com.pubnub.api;

public class SubscribeData {
	public Object message;
	public String timetoken;
	public String toString() {
		System.out.println("subscribe data to string " + message );
		String s = "";
		s = s + "Timetoken: " + timetoken + "\n";
		s = s + "Message: " + message + "\n";

		return s;
	}
}
