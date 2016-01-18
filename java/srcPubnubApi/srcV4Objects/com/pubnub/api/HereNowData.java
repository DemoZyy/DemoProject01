package com.pubnub.api;

public class HereNowData {
	public int occupancy;
	public int total_occupancy;
	public String[] uuids;
	
	public String toString() {

		String s = "";
		s = s + "Occupancy: " + occupancy + "\n";
		s = s + "Total Occupancy: " + occupancy + "\n";
		s = s + "UUIDS: " + PubnubUtil.joinString(uuids, ", ") + "\n";

		return s;
	}
}
