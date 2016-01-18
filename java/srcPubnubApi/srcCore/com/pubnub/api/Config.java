package com.pubnub.api;

public class Config {
	public boolean TLS;
	public String uuid;
	public String authKey;
	public String origin;
	
	public String toString() {
		String s = "";

		s = s + "TLS: " + TLS + "\n";
		s = s + "UUID: " + uuid + "\n";
		s = s + "Auth Key: " + authKey + "\n";
		s = s + "Origin: " + origin + "\n";

		return s;
	}
}
