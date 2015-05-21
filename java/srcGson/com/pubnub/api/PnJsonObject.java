package com.pubnub.api;

import java.io.StringReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


class PnJsonObject extends PnJsonElement {
	
	JsonObject jso;
	
	JsonParser jsonParser = new JsonParser();
	
	public PnJsonObject() {
		jso = new JsonObject();
	}

	public PnJsonObject(Object o) {
		jso = (JsonObject) o;
	}
	public PnJsonObject(String text) throws PnJsonException {
		try {
			JsonReader jsr = new JsonReader(new StringReader(text));
			jsr.setLenient(true);
			this.jso = (JsonObject) jsonParser.parse(jsr);
		}		catch (Exception e) {
			throw new PnJsonException(e);
		}
	}

	public void put(String key, Object value) throws PnJsonException {
		
	}
	
	public Object getBaseObject() {
		return jso;
	}

	public String getString(String key) throws PnJsonException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get(String key) throws PnJsonException {
		try {
			return jso.get(key);
		} catch (JsonParseException e) {
			throw new PnJsonException(e);
		}
	}

	public void remove(String key) {
		jso.remove(key);
	}

	public int length() {
		return jso.entrySet().size();
	}

	public PnJsonObject getJSONObject(String key) throws PnJsonException {
		try {
			return new PnJsonObject(jso.get(key).toString());
		} catch (JsonParseException e) {
			throw new PnJsonException(e);
		}
	}
	
	public String toString() {
		String s = jso.toString();
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
        	s = s.substring(1, s.length() - 1);
        }
        return s;
	}
	
	public static String removeExtraQuotes(String s) {
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
        	s = s.substring(1, s.length() - 1);
        }
        return s;	
	}
}
