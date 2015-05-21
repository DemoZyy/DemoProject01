package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;

class PnJsonArray extends PnJsonElement {
	
	JSONArray jsa;

	public PnJsonArray() {
		jsa = new JSONArray();
	}
	
	public PnJsonArray(String text) throws PnJsonException {
		try {
			jsa = new JSONArray(text);
		} catch (JSONException e) {
			throw new PnJsonException(e);
		}
	}

	
	public PnJsonArray(Object o) {
		jsa = (JSONArray) o;
	}
	

	public Object get(int i) throws PnJsonException {
		try {
			return jsa.get(i);
		} catch (org.json.JSONException e) {
			throw new PnJsonException(e);
		}
	}

	public void put(int i, Object o) throws PnJsonException {
		try {
			jsa.put(i,o);
		} catch (org.json.JSONException e) {
			throw new PnJsonException(e);
		}
	}

	public int length() {
		return jsa.length();
	}

	public String getString(int i) {
		return "";
	}

	public PnJsonArray put(int value) {
		jsa.put(value);
		return this;
	}

	public PnJsonArray put(String value) {
		jsa.put(value);
		return this;
	}

	
	public PnJsonArray put(Object value) {
		jsa.put(value);
		return this;
	}
	
	public Object getBaseObject() {
		return jsa;
	}
	
	public String toString() {
		String s = jsa.toString();
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
        	s = s.substring(1, s.length() - 1);
        }
        return s;
	}
	
}
