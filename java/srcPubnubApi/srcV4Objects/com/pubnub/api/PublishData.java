package com.pubnub.api;

public class PublishData {
	public String timetoken;
	public String description;
	
	public String toString() {
		if (timetoken == null && description == null) 
			return "";
		
		String s = "";
		s = s + "Timetoken: " + timetoken + "\n";
		s = s + "Description: " + description + "\n";
		return s;
	}
}
