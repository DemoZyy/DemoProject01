package com.pubnub.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class DataSyncTransaction {
	String transId;
	JSONArray updatesArray = new JSONArray();
	boolean transactionComplete = false;
}

public class PubnubSyncedObject extends JSONObject {
	private Callback callback;
	private String objectId;
	private String path = "";
	private int depth = 0;
	private String slashPath = "";
	JSONObject meta = new JSONObject();
	private Hashtable transactionsTable = new Hashtable();
	private boolean stale = true;
	private boolean synced = false;

	private PubnubCore pubnub;

	PubnubCore getPubnub() {
		return pubnub;
	}

	void setPubnub(PubnubCore pubnub) {
		this.pubnub = pubnub;
	}

	private PubnubSyncedObject o = this;

	public PubnubSyncedObject(String objectId, String path) {
		this.objectId = objectId;
		this.path     = path; 

		if (this.path != null && this.path.length() > 0) {
			depth = PubnubUtil.splitString(this.path, ".").length;
			this.slashPath = this.path.replace(".", "/");
		}
	}
	
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
		String location = update.getString("location");
		String[] path = PubnubUtil.splitString(location, ".");
		JSONObject[] pathNodes = new JSONObject[path.length];
		String last = path[path.length - 1];
		JSONObject x = o;
		pathNodes[0] = o;

		for (int i = 1 + depth; i < path.length - 1; i++) {
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
			for (int i = path.length - 2; i >= 2 + depth; i--) {
				if (pathNodes[i] != null && pathNodes[i].length() == 0) {
					pathNodes[i-1].remove(path[i]);
				}
			}

			if (pathNodes[1+depth] != null && pathNodes[1+depth].length() == 0)
				o.remove(path[1+depth]);
		}
		if (meta != null) meta.put("last_update", update.getLong("timetoken"));

	}

	private void applyTransaction(JSONObject o, DataSyncTransaction dst,
			Callback callback) throws JSONException {
		
		JSONArray updatesArray = dst.updatesArray;
		
		for (int i = 0; i < updatesArray.length(); i++) {
			JSONObject update = (JSONObject) updatesArray.get(i);
			applyUpdate(o, update);
			update.remove("trans_id");
			update.remove("timetoken");
			String location = getStringFromJSONObject(update, "location");
			if (location != null) {
				location = location.substring(6);
				update.put("location", location);
			}
		}
		((PubnubSyncedObject)o).setStale(false);
		if (callback != null) callback.successCallback("", updatesArray);
	}
	
	private void applyAllTransactions(Callback callback) throws JSONException {
		Iterator keys = transactionsTable.entrySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			DataSyncTransaction dst = (DataSyncTransaction) transactionsTable.get(key);
			if (dst != null && dst.transactionComplete) {
				applyTransaction(o, dst, callback);
				transactionsTable.remove(key);
			}
		}
	}

	public void initSync(final Callback callback) throws PubnubException {
		this.callback = callback;

		try {
			long timetoken = 0;
			if (meta != null) timetoken = meta.getLong("last_update");
			String[] channels = new String[]{
					"pn_ds_" + objectId + ((path != null && path.length() > 0)?"." + path:""),
                    "pn_ds_" + objectId + ((path != null && path.length() > 0)?"." + path:"") + ".*",
                    "pn_dstr_" + objectId
			};
			
			pubnub.subscribe(channels, new Callback() {
				public void connectCallback(String channel, Object response) {
					if (((String)channel).equals("pn_ds_" + objectId + 
							((path != null && path.length() > 0)?"." + path:"")) 
							&& !synced) {
						sync(new Callback(){
							public void successCallback(String channel, Object response) {
								//callback.successCallback("", response);
								try {
									applyAllTransactions(null);
									synced = true;
									setStale(false);
									callback.connectCallback("", objectId);
								} catch (JSONException e) {
									callback.errorCallback("",
											PubnubError.getErrorObject(
													PubnubError.PNERROBJ_INVALID_JSON, 20));
								}
							}
							public void errorCallback(String channel, PubnubError error) {
								callback.errorCallback("", error);
								synced = false;
							}
						});
					}
				}
				public void successCallback(String channel, Object response) {
					JSONObject update = (JSONObject) response;
					if (getStringFromJSONObject(update, "action") != null) {
						// save update
						try {
							String transactionId = update.getString("trans_id");
							DataSyncTransaction dst = (DataSyncTransaction) transactionsTable.get(transactionId);
							if (dst == null) {
								dst = new DataSyncTransaction();
								transactionsTable.put(transactionId, dst);
							}
							dst.updatesArray.put(update);
							
						} catch (JSONException e) {
							callback.errorCallback("",
									PubnubError.getErrorObject(
											PubnubError.PNERROBJ_INVALID_JSON, 14));
						}
					} else if ("complete".equals(getStringFromJSONObject(update, "status"))) {
						// transaction complete
						String transactionId = null;
						try {
							transactionId = update.getString("trans_id");
						} catch (JSONException e1) {
							callback.errorCallback("",
									PubnubError.getErrorObject(
											PubnubError.PNERROBJ_INVALID_JSON, 17));
						}

						if (transactionId == null) return;
						DataSyncTransaction dst = (DataSyncTransaction) transactionsTable.get(transactionId);
						if (dst == null) return;
						dst.transactionComplete = true;
						try {
							if (synced) {
								applyTransaction(o, dst, callback);
								transactionsTable.remove(transactionId);
							}
						} catch (JSONException e) {
							callback.errorCallback("",
									PubnubError.getErrorObject(
											PubnubError.PNERROBJ_INVALID_JSON, 16));
						}
					}
					
					

				}

				public void errorCallback(String channel, PubnubError error) {
					o.setStale(true);
					callback.errorCallback(channel, error);
				}
			}, timetoken);
		} catch (JSONException e1) {
			callback.errorCallback("",
					PubnubError.getErrorObject(
							PubnubError.PNERROBJ_INVALID_JSON, 13));
		}
	}

	private static JSONObject deepMerge(JSONObject target, JSONObject source) throws JSONException {
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
	
	private void sync(final Callback callback) {
		try {
			o.put("pn_ds_meta", new JSONObject());
			meta = o.getJSONObject("pn_ds_meta");
			meta.put("last_update", 0);
		} catch (JSONException e1) {
			callback.errorCallback("",
					PubnubError.getErrorObject(
							PubnubError.PNERROBJ_INVALID_JSON, 15));
		}
		pubnub.get(objectId, this.slashPath, new Callback(){
			public void successCallback(String channel, Object response) {
				try {
					deepMerge(o, (JSONObject)response);
				} catch (JSONException e) {
					callback.errorCallback(channel,
							PubnubError.getErrorObject(
									PubnubError.PNERROBJ_INVALID_JSON, 11));
				}
				//o.setStale(false);
				try {
					if (meta != null) meta.put("last_update", System.nanoTime() / 100);
				} catch (JSONException e) {
					callback.errorCallback(channel,
							PubnubError.getErrorObject(
									PubnubError.PNERROBJ_INVALID_JSON, 12));
				}
				callback.successCallback("", "updated");
				//synced = true;
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
		pubnub.merge(objectId, path, jso, callback);
	}
	public void set(String path, JSONObject jso, Callback callback) {
		pubnub.set(objectId, path, jso, callback);
	}
	public void remove(String path , Callback callback) {
		pubnub.remove(objectId, path, callback);
	}
}
