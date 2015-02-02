package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncedObject implements Iterable {
    private Pubnub pubnub;
    private SyncedObjectManager syncedObjectManager;

    private String path;
    private String objectID;
    private String location;

    /**
     * Is object ready
     *
     * false - just created
     * true  - when initial data object synchronization with server is done
     */
    private AtomicBoolean isReady = new AtomicBoolean(false);

    /**
     * Is object connected
     *
     * false - just created
     * true  - when object is connected to updates channels
     */
    private AtomicBoolean isConnected = new AtomicBoolean(false);

    /**
     * Is object explicitly unsubscribed
     *
     * false - just created
     * true  - after #unsubscribe() method invoked
     */
    private AtomicBoolean isUnsubscribed = new AtomicBoolean(false);

    public final static String TYPE_LIST = "List";
    public final static String TYPE_OBJECT = "Object";
    public final static String TYPE_INTEGER = "Integer";
    public final static String TYPE_STRING = "String";
    public final static String TYPE_BOOLEAN = "Boolean";

    public SyncedObject(SyncedObjectManager manager, String objectID) {
        this(manager, objectID, "");
    }

    public SyncedObject(SyncedObjectManager manager, String objectID, String path) {
        this.syncedObjectManager = manager;
        this.pubnub = manager.getPubnub();
        this.objectID = objectID;
        this.path = path;
        this.location = SyncedObject.glue(objectID, path);
    }

    public String getPath() {
        return path;
    }

    public String getObjectID() {
        return objectID;
    }

    public String getLocation() {
        return location;
    }

    public boolean getIsConnected() {
        return isConnected.get();
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected.set(isConnected);
    }

    public boolean getIsReady() {
        return isReady.get();
    }

    public void setIsReady(boolean isReady) {
        this.isReady.set(isReady);
    }

    public boolean getIsUnsubscribed() {
        return this.isUnsubscribed.get();
    }

    /**
     * Returns the value mapped by name, or throws if no such mapping exists.
     *
     * @param relativePath to element
     * @return object
     * @throws PubnubException
     */
    public synchronized Object get(String relativePath) throws PubnubException {
        return getValue(relativePath);
    }

    /**
     * Returns the value mapped by name, or throws if no such mapping exists.
     *
     * @return object
     */
    public synchronized Object get() {
        try {
            return getValue("");
        } catch (PubnubException e) {
            return null;
        }
    }

    /**
     * Returns the value mapped by name, or null if no such mapping exists.
     *
     * @param relativePath to element
     * @return object or null
     */
    public synchronized Object opt(String relativePath) {
        try {
            return getValue(relativePath);
        } catch (PubnubException e) {
            return null;
        }
    }

    /**
     * Returns the value mapped by name, or fallback if no such mapping exists.
     *
     * @param relativePath to element
     * @return object
     */
    public synchronized Object opt(String relativePath, Object fallback) {
        try {
            return getValue(relativePath);
        } catch (PubnubException e) {
            return fallback;
        }
    }

    /**
     * Returns the value mapped by name if it exists, coercing it if necessary, or throws if no such mapping exists.
     *
     * @param relativePath to value
     * @return value as string
     * @throws PubnubException
     */
    public synchronized String getString(String relativePath) throws PubnubException {
        return getValue(relativePath).toString();
    }

    /**
     * Returns the mapped value, coercing it if necessary.
     *
     * @return value as string
     */
    public synchronized String getString() {
        try {
            return getString("");
        } catch (PubnubException e) {
            return null;
        }
    }

    /**
     * Returns the value mapped by name if it exists, coercing it if necessary, or the empty string if no such mapping exists.
     *
     * @param relativePath to value
     * @return value as string
     */
    public synchronized String optString(String relativePath) {
        try {
            return getString(relativePath);
        } catch (PubnubException e) {
            return "";
        }
    }

    /**
     * Returns the value mapped by name if it exists, coercing it if necessary, or fallback if no such mapping exists.
     *
     * @param relativePath to value
     * @param fallback value
     * @return value as string
     */
    public synchronized String optString(String relativePath, String fallback) {
        try {
            return getString(relativePath);
        } catch (PubnubException e) {
            return fallback;
        }
    }

    /**
     * Returns the value mapped by name if it exists, coercing it if necessary, or throws if no such mapping exists.
     *
     * @param relativePath to value
     * @return value as int
     */
    public synchronized int getInteger(String relativePath) throws PubnubException {
        return Integer.parseInt(getValue(relativePath).toString());
    }

    /**
     * Returns the mapped value, coercing it if necessary.
     *
     * @return value as int
     */
    public synchronized int getInteger() {
        try {
            return Integer.parseInt(getValue("").toString());
        } catch (PubnubException e) {
            return 0;
        }
    }

    /**
     * Returns the value mapped by name if it exists and is an int or can be coerced to an int, or 0 otherwise.
     *
     * @param relativePath to value
     * @return integer
     */
    public synchronized int optInteger(String relativePath) {
        try {
            return getInteger(relativePath);
        } catch (PubnubException e) {
            return 0;
        }
    }

    /**
     * Returns the value mapped by name if it exists and is an int or can be coerced to an int, or fallback otherwise.
     *
     * @param relativePath to value
     * @param fallback value
     * @return integer
     */
    public synchronized int optInteger(String relativePath, int fallback) {
        try {
            return getInteger(relativePath);
        } catch (PubnubException e) {
            return fallback;
        }
    }

    /**
     * Returns the value mapped by name if it exists and is a boolean or can be coerced to a boolean, or throws otherwise.
     *
     * @param relativePath to element
     * @return value as boolean
     * @throws PubnubException
     */
    public synchronized boolean getBoolean(String relativePath) throws PubnubException {
        return (Boolean) getValue(relativePath);
    }

    /**
     * Returns the mapped value
     *
     * @return value as boolean
     */
    public synchronized boolean getBoolean() {
        try {
            return getBoolean("");
        } catch (PubnubException e) {
            return false;
        }
    }

    /**
     * Returns the value mapped by name if it exists and is a boolean or can be coerced to a boolean, or false otherwise.
     *
     * @param relativePath to element
     * @return value or false
     */
    public synchronized boolean optBoolean(String relativePath) {
        try {
            return getBoolean(relativePath);
        } catch (PubnubException e) {
            return false;
        }
    }

    /**
     * Returns the value mapped by name if it exists and is a boolean or can be coerced to a boolean, or fallback otherwise.
     *
     * @param relativePath to element
     * @param fallback value
     * @return value or fallback
     */
    public synchronized boolean optBoolean(String relativePath, boolean fallback) {
        try {
            return getBoolean(relativePath);
        } catch (PubnubException e) {
            return fallback;
        }
    }

    public synchronized ArrayList getList() {
        return getList(null);
    }

    public synchronized ArrayList getList(String relativePath) {
        try {
            return (ArrayList) getValue(relativePath);
        } catch (PubnubException e) {
            return null;
        }
    }

    public synchronized HashMap getMap() {
        return getMap(null);
    }

    public synchronized HashMap getMap(String relativePath) {
        try {
            return (HashMap) getValue(relativePath);
        } catch (PubnubException e) {
            return null;
        }
    }

    private synchronized Object getValue(String relativePath) throws PubnubException {
        try {
            return syncedObjectManager.getValue(glue(location, relativePath));
        } catch (JSONException e) {
            throw new PubnubException("Unable to reach element by specified location: \"" + location + "\"");
        }
    }

    public synchronized Object pop() {
        return pop(null);
    }

    public synchronized Object pop(Callback callback) {
        try {
            String lastKey = syncedObjectManager.lastListKey(location);
            Object result = syncedObjectManager.getValue(glue(location, lastKey));
            if (callback == null) {
                remove(lastKey);
            } else {
                remove(lastKey);
            }
            return result;
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized Object shift() {
        try {
            String firstKey = syncedObjectManager.firstListKey(location);
            Object result = syncedObjectManager.getValue(glue(location, firstKey));
            remove(firstKey);
            return result;
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized Object removeByIndex(Integer index) {
        String key = getKeyByIndex(index);

        if (getKeyByIndex(index) != null) {
            try {
                Object result = syncedObjectManager.getValue(glue(location, key));
                remove(key);
                return result;
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public synchronized Object replaceByIndex(Integer index, Object data) {
        String key = getKeyByIndex(index);

        if (getKeyByIndex(index) != null) {
            try {
                Object result = syncedObjectManager.getValue(glue(location, key));
                replace(key, data);
                return result;
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public synchronized Object getByIndex(Integer index) {
        String key = getKeyByIndex(index);

        if (getKeyByIndex(index) != null) {
            try {
                return syncedObjectManager.getValue(glue(location, key));
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public synchronized String getKeyByIndex(Integer index) {
        try {
            JSONObject value = syncedObjectManager.getRawValue(location);
            if (isPnList(value)) {
                int i = 0;
                Iterator valueIterator = PubnubUtil.jsonObjectKeysSortedIterator(value);

                while (valueIterator.hasNext()) {
                    if (index.intValue() == i) {
                        return (String) valueIterator.next();
                    } else {
                        valueIterator.next();
                        i++;
                    }
                }
                return null;
            } else {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized Object removeByKey(String key) {
        try {
            Object value = syncedObjectManager.getValue(glue(location, key));
            remove(key);
            return value;
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized Object replaceByKey(String key, Object data) {
        try {
            Object value = syncedObjectManager.getValue(glue(location, key));
            replace(key, data);
            return value;
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized Object removeByValue(Object searchValue) {
        String key = getKeyByValue(searchValue);

        if (key != null) {
            Object result;
            try {
                result = syncedObjectManager.getValue(glue(location, key));
            } catch (JSONException e) {
                return null;
            }
            remove(key);
            return result;
        } else {
            return null;
        }
    }

    public synchronized Object replaceByValue(Object searchValue, Object data) {
        String key = getKeyByValue(searchValue);

        if (key != null) {
            Object result;
            try {
                result = syncedObjectManager.getValue(glue(location, key));
            } catch (JSONException e) {
                return null;
            }
            replace(key, data);
            return result;
        } else {
            return null;
        }
    }

    public synchronized String getKeyByValue(Object searchValue) {
        if (searchValue == null) return null;

        try {
            JSONObject value = syncedObjectManager.getRawValue(location);

            JSONArray valueKeys = value.names();
            JSONObject currentObject;
            String currentKey;
            String resultKey = null;

            for (int i = 0; i < valueKeys.length(); i++) {
                currentKey = (String) valueKeys.get(i);
                currentObject = value.getJSONObject(currentKey);
                if (currentObject.has("pn_val") && currentObject.get("pn_val").equals(searchValue)) {
                    resultKey = currentKey;
                    break;
                }
            }

            return resultKey;
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized String getType() {
        return getType("");
    }

    public synchronized String getType(String path) {
        try {
            JSONObject value = syncedObjectManager.getRawValue(glue(location, path));

            if (value.has("pn_val")) {
                Object rawValue = value.get("pn_val");
                if (rawValue instanceof String) {
                    return TYPE_STRING;
                } else if (rawValue instanceof Integer) {
                    return TYPE_INTEGER;
                } else if (rawValue instanceof Boolean) {
                    return TYPE_BOOLEAN;
                } else {
                    return null;
                }
            } else if (SyncedObject.isPnList(value)) {
                return TYPE_LIST;
            } else {
                return TYPE_OBJECT;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public synchronized Integer size() {
        return size("");
    }

    public synchronized Integer size(String path) {
        try {
            JSONObject value = syncedObjectManager.getRawValue(glue(location, path));
            if (!value.has("pn_val")) {
                return Integer.valueOf(value.length());
            } else {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Return iterator if synced object instance represents list or object and null if it represents string, integer or boolean
     *
     * @return iterator or null
     */
    public Iterator iterator() {
        try {
            final JSONObject rawValue = syncedObjectManager.getRawValue(location);

            if (rawValue.has("pn_val")) {
                return null;
            }

            if (isPnList(rawValue)) {
                return new SyncedObjectIterator(this, PubnubUtil.jsonObjectKeysSortedIterator(rawValue));
            } else {
                return new SyncedObjectIterator(this, rawValue.keys());
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Return child synced object.
     *
     * @param relativePath - relative path
     * @param callback     - callback
     * @return child object
     */
    public SyncedObject child(String relativePath, DataSyncCallback callback) {
        return syncedObjectManager.add(objectID, glue(path, relativePath), callback);
    }

    public SyncedObject child(String relativePath) {
        return child(relativePath, null);
    }

    public void merge(Object data) {
        merge("", data);
    }

    public void merge(String path, Object data) {
        merge(path, data, new Callback() {
        });
    }

    public void merge(String path, Object data, final Callback callback) {
        Hashtable args = new Hashtable();

        args.put("location", glue(location, path));
        args.put("data", data);

        pubnub.merge(args, callback);
    }

    public void replace(Object data) {
        replace("", data);
    }

    public void replace(String path, Object data) {
        replace(path, data, new Callback() {
        });
    }

    public void replace(String path, Object data, Callback callback) {
        Hashtable args = new Hashtable();

        args.put("location", glue(location, path));
        args.put("data", data);

        pubnub.replace(args, callback);
    }

    public void push(Object data) {
        push("", data);
    }

    public void push(String path, Object data) {
        push(path, data, new Callback() {
        });
    }

    public void push(String path, Object data, Callback callback) {
        Hashtable args = new Hashtable();

        args.put("location", glue(location, path));
        args.put("data", data);

        pubnub.push(args, callback);
    }

    public void push(String path, Object data, String key) {
        push(path, data, key, new Callback() {
        });
    }

    public void push(Object data, String key, Callback callback) {
        push("", data, key, callback);
    }

    public void push(String path, Object data, String key, Callback callback) {
        Hashtable args = new Hashtable();

        args.put("location", glue(location, path));
        args.put("data", data);
        args.put("sort_key", key);

        pubnub.push(args, callback);
    }

    public void remove(String path) {
        remove(path, new Callback() {
        });
    }

    public void remove(String path, Callback callback) {
        Hashtable args = new Hashtable();

        args.put("location", glue(location, path));

        pubnub.remove(args, callback);
    }

    public void remove() {
        this.syncedObjectManager.remove(location);
        this.isUnsubscribed.set(true);
    }

    public void unsubscribe() {
        this.syncedObjectManager.unsubscribe(location);
        this.isUnsubscribed.set(true);
    }

    public static String getURLizedObjectPath(String location) {
        String[] path = PubnubUtil.splitString(location, ".");

        for (int i = 0; i < path.length - 1; i++) {
            path[i] = PubnubUtil.urlEncode(path[i]);
        }

        return PubnubUtil.joinString(path, "/");
    }

    public static String glue(String first, String second) {
        if (PubnubUtil.isPresent(first) && PubnubUtil.isPresent(second)) {
            return first + "." + second;
        } else if (PubnubUtil.isPresent(first) && PubnubUtil.isBlank(second)) {
            return first;
        } else if (PubnubUtil.isBlank(first) && PubnubUtil.isPresent(second)) {
            return second;
        } else {
            return "";
        }
    }

    public static boolean isPnList(JSONObject item) {
        if (item == null) {
            return false;
        }

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (item) {
            JSONArray itemNames = item.names();

            for (int i = 0; i < itemNames.length(); i++) {
                try {
                    if (itemNames.getString(i).indexOf("-") == 0 && itemNames.getString(i).indexOf("!") > 0)
                        return true;
                } catch (JSONException e) {
                    return false;
                }
            }
        }

        return false;
    }
}
