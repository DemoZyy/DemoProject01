package com.pubnub.api;

import com.google.gson.JsonParseException;

public class PnJsonException extends Exception {

	Exception jse;
	
	public PnJsonException(JsonParseException e) {
		this.jse = e;
	}

	public PnJsonException(ClassCastException e) {
		this.jse = e;
	}

	public PnJsonException(Exception e) {
		this.jse = e;
	}


}
