package com.pubnub.api;

public class PublishData {
	public String timetoken;
	public String information;
	
	public String toString() {
		if (timetoken == null && information == null) 
			return "";
		
		String s = "";
		s = s + "Timetoken: " + timetoken + "\n";
		s = s + "Information: " + information + "\n";
		return s;
	}
}
