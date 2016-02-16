package com.pubnub.api;

public class HereNowResult extends Result {
	HereNowData data;

	public HereNowResult() {
		data = new HereNowData();
	}
	
	public HereNowData getData() {
		return data;
	}

	void setData(HereNowData data) {
		this.data = data;
	}

	public String toString() {
		String s = super.toString();
		s = s + data + "\n";
		return s;
		
	}
}
