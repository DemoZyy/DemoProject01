package com.pubnub.api;

import com.google.gson.JsonParseException;

public class PnJsonException extends Exception {

	JsonParseException jse;
	
	public PnJsonException(JsonParseException e) {
		this.jse = e;
	}


}
