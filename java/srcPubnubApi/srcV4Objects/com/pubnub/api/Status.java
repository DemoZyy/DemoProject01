package com.pubnub.api;

class Status {
	boolean wasAutoRetried;
	boolean isError;
	StatusCategory category;
	
	public String toString() {
		String s = "";
		s = s + "Was Auto Retried ? : " + wasAutoRetried + "\n"; 
		s = s + "Is Error ? : " + isError + "\n";
		s = s + "Category : " + category + "\n";
		
		return s;
	}
}
