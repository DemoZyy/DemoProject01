package com.pubnub.api;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PubnubSyncedObject extends JSONObject {
	private boolean state = false;
	private Callback callback;
	private String objectId;
	private boolean stale = true;

	private PubnubCore pubnub;

	PubnubCore getPubnub() {
		return pubnub;
	}

	void setPubnub(PubnubCore pubnub) {
		this.pubnub = pubnub;
	}

	private PubnubSyncedObject o = this;

	public PubnubSyncedObject(String objectId) {
		this.objectId = objectId;
	}

	public Callback getCallback() {
		return callback;
	}

	private String getStringFromJSONObject(JSONObject o, String key) {
		try {
			return o.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject getJSONObjectFromJSONObject(JSONObject o, String key) {
		try {
			return o.getJSONObject(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private Object getObjectFromJSONObject(JSONObject o, String key) {
		try {
			return o.get(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private void applyUpdate(JSONObject o, JSONObject update) throws JSONException {
		System.out.println(update);
		String location = update.getString("location");
		String[] path = PubnubUtil.splitString(location, ".");
		JSONObject[] pathNodes = new JSONObject[path.length];
		String last = path[path.length - 1];
		JSONObject x = o;
		pathNodes[0] = o;
		for (int i = 1; i < path.length - 1; i++) {
			String key = path[i];
			if (getJSONObjectFromJSONObject(x, key) == null) {
				x.put(key, new JSONObject());
			}
			x = x.getJSONObject(key);
			pathNodes[i] = x;
		}

		if (update.getString("action").equals("update")) {
			x.put(last, update.get("value"));
		} else if (update.getString("action").equals("delete")) {
			x.remove(last);
			for (int i = path.length - 2; i >= 2; i--) {
				if (pathNodes[i] != null && pathNodes[i].length() == 0) {
					pathNodes[i-1].remove(path[i]);
				}
			}
			if (pathNodes[1] != null && pathNodes[1].length() == 0)
				o.remove(path[1]);
		}
		o.put("last_update", update.getLong("timetoken"));

	}

	private void applyUpdates(JSONObject o, JSONObject updates,
			Callback callback) throws JSONException {

		Iterator keys = updates.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String action = "update";
			JSONArray updatesArray = updates.getJSONArray(key);
			for (int i = 0; i < updatesArray.length(); i++) {
				applyUpdate(o, updatesArray.getJSONObject(i));
				action = updatesArray.getJSONObject(i).getString("action");
			}
			callback.successCallback("", action);
			updates.remove(key);
		}
	}

	public void setCallback(final Callback callback) throws PubnubException {
		this.callback = callback;

		try {
			long timetoken = o.getLong("last_update");
			pubnub.subscribe("pn_ds_" + objectId, new Callback() {
				public void successCallback(String channel, Object response) {
					JSONObject update = (JSONObject) response;
					try {
						applyUpdate(o, update);
					} catch (JSONException e) {
						callback.errorCallback("",
								PubnubError.getErrorObject(
										PubnubError.PNERROBJ_INVALID_JSON, 14));
					}
					try {
						JSONObject callbackData = new JSONObject();
						callbackData.put("location", update.getString("location"));
						callbackData.put("action", update.getString("action"));
						callback.successCallback("", callbackData);
						o.setStale(false);
					} catch (JSONException jse) {
						callback.errorCallback(channel,
								PubnubError.getErrorObject(
										PubnubError.PNERROBJ_INVALID_JSON, 10));
					}
				}

				public void errorCallback(String channel, PubnubError error) {
					o.setStale(true);
					callback.errorCallback(channel, error);
				}
			}, timetoken);
		} catch (JSONException e1) {
			e1.printStackTrace();
			callback.errorCallback("",
					PubnubError.getErrorObject(
							PubnubError.PNERROBJ_INVALID_JSON, 13));
		}
	}

	public boolean isState() {
		return state;
	}

	public static JSONObject deepMerge(JSONObject target, JSONObject source) throws JSONException {
		Iterator keys = source.keys();
		while(keys.hasNext()) {
			String key = (String) keys.next();
            Object value = source.get(key);
            if (!target.has(key)) {
                target.put(key, value);
            } else {
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject)value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
		}
	    return target;
	}
	
	public void sync(final Callback callback) {
		pubnub.read(objectId, "", new Callback(){
			public void successCallback(String channel, Object response) {
				try {
					deepMerge(o, (JSONObject)response);
				} catch (JSONException e) {
					callback.errorCallback(channel,
							PubnubError.getErrorObject(
									PubnubError.PNERROBJ_INVALID_JSON, 11));
				}
				o.setStale(false);
				try {
					o.put("last_update", System.nanoTime() / 100);
				} catch (JSONException e) {
					callback.errorCallback(channel,
							PubnubError.getErrorObject(
									PubnubError.PNERROBJ_INVALID_JSON, 12));
				}
				callback.successCallback("", "updated");
			}
			public void errorCallback(String channel, PubnubError response) {
				o.setStale(true);
				callback.errorCallback(channel, response);
			}
		});
	}

	public boolean isStale() {
		return stale;
	}

	void setStale(boolean stale) {
		this.stale = stale;
	}
	public void merge(String path, JSONObject jso, Callback callback) {
		pubnub.write(objectId, path, jso, callback);
	}
	public void delete(String path , Callback callback) {
		pubnub.delete(objectId, path, callback);
	}
}