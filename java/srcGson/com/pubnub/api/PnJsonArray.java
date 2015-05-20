package com.pubnub.api;



import java.io.StringReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

class PnJsonArray {
	
	JsonArray jsa;
	
	JsonParser jsonParser = new JsonParser();

	public PnJsonArray() {
		jsa = new JsonArray();
	}
	
	public PnJsonArray(Object o) {
		jsa = (JsonArray) o;
	}
	
	
	public PnJsonArray(String text) throws PnJsonException {
		try {
			JsonReader jsr = new JsonReader(new StringReader(text));
			jsr.setLenient(true);
			jsa = (JsonArray) jsonParser.parse(jsr);
		} catch (JsonParseException e) {
			throw new PnJsonException(e);
		}
	}

	public Object get(int i) throws PnJsonException {
		return jsa.get(i);
	}

	public void put(int i, Object o) throws PnJsonException {
		try {
			JsonReader jsr = new JsonReader(new StringReader(o.toString()));
			jsr.setLenient(true);
			jsa.set(i, jsonParser.parse(jsr));
		} catch (JsonParseException e) {
			throw new PnJsonException(e);
		}
	}

	public int length() {
		return jsa.size();
	}

	public String getString(int i) {
		return jsa.get(i).getAsString();
	}
	
	public String toString() { 
		return jsa.toString() ;
	}

	public PnJsonArray put(int value) {
		this.put(String.valueOf(value));
		return this;
	}

	public PnJsonArray put(String value) {
		JsonReader jsr = new JsonReader(new StringReader(value));
		jsr.setLenient(true);
		jsa.add(jsonParser.parse(jsr));
		return this;
	}

	
	public PnJsonArray put(Object value) {
		this.put(value.toString());
		return this;
	}
	
	public Object getBaseObject() {
		return jsa;
	}
}
