package com.pubnub.api;

public class StatusUtil {
	public String getString(StatusInterface statusInterface) {
		String s = "";
		s = s + "Is Error ?: " + statusInterface.isError() + "\n";
		s = s + "Was AutoRetried: " + statusInterface.wasAutoRetried() + "\n";
		s = s + "StatusInterface Category: " + statusInterface.getCategory() + "\n";
		
		return s;
	}
}
