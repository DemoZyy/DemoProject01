package com.pubnub.api;

class PnJsonException extends Exception {

	org.json.JSONException jse;
	
	public PnJsonException(org.json.JSONException e) {
		this.jse = e;
	}


}
