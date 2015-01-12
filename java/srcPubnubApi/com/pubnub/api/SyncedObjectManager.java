package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SyncedObjectManager {
    private Pubnub pubnub;
    private Hashtable<String, SyncedObject> syncedObjects;
    private JSONObject data;
    private HashMap<String, DataSyncCallback> callbacks;
    private HashSet<String> channels;
    private HashMap updates;
    private HashSet<String> objectsSyncPending;

    private final static String DS_SUBSCRIBE_RESPONSE_ACTION = "action";
    private final static String DS_SUBSCRIBE_RESPONSE_STATUS = "status";
    private final static String DS_SUBSCRIBE_STATUS_COMPLETE = "complete";

    public final static String ACTION_MERGE = "merge";
    public final static String ACTION_REPLACE = "replace";
    public final static String ACTION_REPLACE_DELETE = "replace-delete";
    public final static String ACTION_PUSH = "push";
    public final static String ACTION_REMOVE = "delete";

    public SyncedObjectManager(PubnubCore pubnub) {
        this.pubnub = (Pubnub) pubnub;
        this.syncedObjects = new Hashtable<String, SyncedObject>();
        this.data = new JSONObject();
        this.callbacks = new HashMap<String, DataSyncCallback>();
        this.channels = new HashSet<String>();
        this.updates = new HashMap();
        this.objectsSyncPending = new HashSet<String>();
    }

    public SyncedObject add(String objectID, String path, DataSyncCallback callback) {
        String location = SyncedObject.glue(objectID, path);
        SyncedObject parentObject = getAlreadySubscribedObjectIfExists(location);
        SyncedObject object;

        this.channels.add(location);
        if (callback != null) {
            this.callbacks.put(location, callback);
        }

        object = new SyncedObject(this, objectID, path);

        this.syncedObjects.put(object.getLocation(), object);

        if (parentObject == null) {
            this.objectsSyncPending.add(location);
            subscribe(callback);
        } else if (callback != null) {
            object.setIsReady(true);
            callback.connectCallback(location);
            callback.invokeReadyCallback(object);
        }

        return object;
    }


    public Pubnub getPubnub() {
        return pubnub;
    }

    public SyncedObject getAlreadySubscribedObjectIfExists(String location) {
        if (syncedObjects.containsKey(location)) {
            return (SyncedObject) syncedObjects.get(location);
        }

        Enumeration chs = syncedObjects.keys();
        String channel;

        while (chs.hasMoreElements()) {
            channel = (String) chs.nextElement();
            if (location.indexOf(channel + ".") == 0) {
                return (SyncedObject) syncedObjects.get(channel);
            }
        }

        return null;
    }

    protected JSONObject getRawValue(String location) throws JSONException {
        String[] locationParts = PubnubUtil.splitString(location, ".");
        JSONObject current = data;

        for (int i = 0; i < locationParts.length; i++) {
            current = current.getJSONObject(locationParts[i]);
        }

        return current;
    }

    public Object getValue(String location) throws JSONException {
        return parseObject(getRawValue(location));
    }

    public String firstListKey(String location) throws JSONException {
        JSONObject value = getRawValue(location);

        return (String) value.sortedKeys().next();
    }

    public String lastListKey(String location) throws JSONException {
        JSONObject value = getRawValue(location);
        Iterator valueKeys = value.sortedKeys();
        String lastKey = null;

        while (valueKeys.hasNext()) {
            lastKey = (String) valueKeys.next();
        }

        return lastKey;
    }

    public void invokeActionCallbacks(ArrayList cbs, String action, String updatedAt, List data) {
        Iterator cbsIterator = cbs.iterator();

        while (cbsIterator.hasNext()) {
            DataSyncCallback cb = (DataSyncCallback) cbsIterator.next();
            if (action.equals(ACTION_MERGE)) {
                cb.mergeCallback(data, updatedAt);
            } else if (action.equals(ACTION_REPLACE)) {
                cb.replaceCallback(data, updatedAt);
            } else if (action.equals(ACTION_REMOVE)) {
                cb.removeCallback(data, updatedAt);
            }
        }
    }

    public ArrayList getCallbacksByLocation(String location) {
        Iterator keys = callbacks.entrySet().iterator();
        ArrayList resultCallbacks = new ArrayList();
        String currentLocation;

        while (keys.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) keys.next();
            currentLocation = (String) entry.getKey();
            if (currentLocation != null) {
                if (location.indexOf(currentLocation) == 0) {
                    resultCallbacks.add(callbacks.get(currentLocation));
                }
            }
        }

        return resultCallbacks;
    }

    public String[] getChannelsForSubscribe() {
        HashSet channelsSet = new HashSet();
        Iterator channelsIterator = this.channels.iterator();
        String[] result;
        String channel;

        while (channelsIterator.hasNext()) {
            channel = (String) channelsIterator.next();

            channelsSet.add("pn_ds_" + channel);
            channelsSet.add("pn_ds_" + channel + ".*");
            channelsSet.add("pn_dstr_" + PubnubUtil.splitString(channel, ".")[0]);
        }

        result = new String[channelsSet.size()];
        channelsSet.toArray(result);
        return result;
    }

    public String[] getChannelsForUnsubscribe(String objectId) {
        return new String[]{
                "pn_ds_" + objectId,
                "pn_ds_" + objectId + ".*",
                "pn_dstr_" + PubnubUtil.splitString(objectId, ".")[0]
        };
    }

    private void subscribe(final DataSyncCallback callback) {
        try {
            pubnub.subscribe(
                    getChannelsForSubscribe(),
                    new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            if (!channel.contains("dstr") && channel.indexOf('*') < 0) {
                                channel = channel.substring(6);
                                ArrayList locationElements = new ArrayList(Arrays.asList(PubnubUtil.splitString(channel, ".")));
                                String objectId = (String) locationElements.remove(0);
                                String path = PubnubUtil.joinString(locationElements, ".");

                                if (callback != null) {
                                    callback.connectCallback(channel);
                                }

                                fetchObject(objectId, path, callback);
                            }
                        }

                        @Override
                        public void successCallback(String channel, Object data) {
                            JSONObject message = (JSONObject) data;

                            if (message.has(DS_SUBSCRIBE_RESPONSE_ACTION)) {
                                SyncedObjectDelta update = new SyncedObjectDelta(message, channel);
                                SyncedObjectUpdatesList list;
                                String transaction = update.getTransID().toString();

                                if (!updates.containsKey(transaction)) {
                                    list = new SyncedObjectUpdatesList();
                                    updates.put(transaction, list);
                                } else {
                                    list = (SyncedObjectUpdatesList) updates.get(transaction);
                                }

                                list.add(update);
                            } else if (message.has(DS_SUBSCRIBE_RESPONSE_STATUS)) {
                                String status;
                                String transaction;

                                try {
                                    status = message.getString(DS_SUBSCRIBE_RESPONSE_STATUS);
                                } catch (JSONException e) {
                                    status = null;
                                }

                                if (!status.equals(DS_SUBSCRIBE_STATUS_COMPLETE)) return;

                                try {
                                    transaction = message.getString("trans_id");
                                } catch (JSONException e) {
                                    // TODO: invoke correct error callback
                                    return;
                                }

                                synchronized (updates) {
                                    SyncedObjectUpdatesList updatesList =
                                            (SyncedObjectUpdatesList) updates.get(transaction);

                                    if (updatesList == null) {
                                        // TODO: invoke correct error callback
                                        return;
                                    }

                                    updatesList.setComplete(true);

                                    SyncedObjectDelta first = (SyncedObjectDelta) updatesList.get(0);

                                    if (first == null) {
                                        // TODO: invoke correct error callback
                                        return;
                                    }

                                    // TODO: if object is blocked, do not invoke
                                    String objectId = PubnubUtil.splitString(first.getLocation(), ".")[0];

                                    if (isObjectSyncPending(objectId)) {
                                        try {
                                            applyUpdates(updatesList);
                                        } catch (JSONException e) {
                                            callback.errorCallback(PubnubError.PNERROBJ_JSON_ERROR);
                                        }

                                        updates.remove(transaction);
                                    }
                                }
                            }
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            callback.errorCallback(error);
                        }

                        @Override
                        public void reconnectCallback(String channel, Object message) {
                            if (callback != null && channel != null && channel.indexOf("pn_dstr_") == 0) {
                                callback.reconnectCallback();
                            }
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            if (callback != null && channel != null && channel.indexOf("pn_dstr_") == 0) {
                                callback.disconnectCallback();
                            }
                        }
                    }
            );

        } catch (PubnubException ex) {
            callback.errorCallback(ex.getPubnubError());
        }
    }

    public void applyAllUpdates() throws JSONException {
        // DANGER: complete transactions of not ready objects can be applied
        synchronized (this.updates) {
            for (int i = 0; i < updates.size(); i++) {
                applyUpdates((SyncedObjectUpdatesList) updates.get(i));
            }
        }
    }

    public void applyUpdates(SyncedObjectUpdatesList updatesList) throws JSONException {
        if (!updatesList.isComplete()) {
            return;
        }

        Iterator updatesIterator = updatesList.iterator();

        // apply updates on object
        while (updatesIterator.hasNext()) {
            applyUpdate((SyncedObjectDelta) updatesIterator.next());
        }

        // invoke associated callback
        SyncedObjectDelta first = (SyncedObjectDelta) updatesList.get(0);
        String action = first.getAction();
        String location = first.getLocation();
        String updatedAt = first.getUpdatedAt();
        ArrayList cbs = getCallbacksByLocation(location);

        if (action.equals(ACTION_MERGE) || action.equals(ACTION_PUSH)) {
            invokeActionCallbacks(cbs, ACTION_MERGE, updatedAt, updatesList);
        } else if (action.equals(ACTION_REMOVE)) {
            Iterator actionsIterator = updatesList.iterator();
            SyncedObjectDelta delta;

            while (actionsIterator.hasNext()) {
                delta = (SyncedObjectDelta) actionsIterator.next();
                if (delta.getAction().equals(ACTION_REPLACE)) {
                    invokeActionCallbacks(cbs, ACTION_REPLACE, updatedAt, updatesList);
                    return;
                }
            }

            invokeActionCallbacks(cbs, ACTION_REMOVE, updatedAt, updatesList);
        } else if (action.equals(ACTION_REPLACE)) {
            invokeActionCallbacks(cbs, ACTION_REPLACE, updatedAt, updatesList);
        }
    }

    public synchronized void applyUpdate(SyncedObjectDelta delta) throws JSONException {
        String action = delta.getAction();

        if (ACTION_MERGE.equals(action)
                || ACTION_REPLACE.equals(action)
                || ACTION_PUSH.equals(action)) {

            JSONObject val = new JSONObject();

            val.put("pn_val", delta.getValue());
            val.put("pn_tt", delta.getTimetoken().toString());

            updateJSONObjectValue(data, val, delta.getLocation());
        } else if (ACTION_REPLACE_DELETE.equals(action)
                || ACTION_REMOVE.equals(action)) {
            updateJSONObjectValue(data, null, delta.getLocation());
        }
    }

    public SyncedObject getObjectById(String objectId) {
        return (SyncedObject) this.syncedObjects.get(objectId);
    }

    public Boolean isObjectSyncPending(String objectId) {
        return this.objectsSyncPending.contains(objectId);
    }

    public void fetchObject(String objectId, String path, final DataSyncCallback callback) {
        fetchObject(objectId, path, callback, null);
    }

    private void fetchObject(final String objectId, final String path, final DataSyncCallback callback, String nextPage) {
        Hashtable args = new Hashtable();
        final String location = SyncedObject.glue(objectId, path);
        args.put("location", location);

        if (nextPage != null) {
            args.put("nextPage", nextPage);
        }

        pubnub.get(args, new Callback() {
            public void successCallback(Object message) {
                JSONObject jsonMessage = ((JSONObject) message);

                try {
                    merge(data, jsonMessage.getJSONObject("data"), location);
                } catch (JSONException e) {
                    callback.errorCallback(PubnubError.PNERROBJ_JSON_ERROR);
                }

                if (jsonMessage.isNull("next_page")) {
                        if (callback != null) {
                            getObjectById(objectId).setIsReady(true);

                            try {
                                applyAllUpdates();
                            } catch (JSONException e) {
                                callback.errorCallback(PubnubError.PNERROBJ_JSON_ERROR);
                            }

                            callback.invokeReadyCallback((SyncedObject) syncedObjects.get(location));
                        }
                    } else {
                        fetchObject(objectId, path, callback, jsonMessage.optString("next_page"));
                    }
            }

            public void errorCallback(PubnubError error) {
                callback.errorCallback(error);
            }
        });
    }

    protected synchronized void unsubscribe(String location) {
        this.callbacks.remove(location);
        this.channels.remove(location);
        this.objectsSyncPending.remove(location);
        this.data.remove(location);

        this.pubnub.unsubscribe(getChannelsForUnsubscribe(location));
    }

    /**
     * Merge JSON objects.
     *
     * @param original  - original object
     * @param newObject - updated values
     * @param location  - location of updates
     */
    public static void merge(JSONObject original, JSONObject newObject, String location) throws JSONException {
        JSONArray newObjectNames = newObject.names();
        JSONObject current = getOrCreatePath(original, location);

        for (int i = 0; i < newObjectNames.length(); i++) {
            String key = newObjectNames.getString(i);

            if (current.has(key)) {
                current.remove(key);
            }

            current.put(key, newObject.get(key));
        }
    }

    /**
     * Get or create json path in original JSONObject.
     *
     * @param original - JSONObject
     * @param path     - path
     * @return JSONObject located at path.
     */
    public static JSONObject getOrCreatePath(JSONObject original, String path) throws JSONException {
        String[] pathElements = PubnubUtil.splitString(path, ".");
        JSONObject current = original;

        for (int i = 0; i < pathElements.length; i++) {
            String key = pathElements[i];

            if (!current.has(key)) {
                current.put(key, new JSONObject());
            }

            current = current.getJSONObject(key);
        }

        return current;
    }

    public static void updateJSONObjectValue(JSONObject original, Object value, String location)
            throws JSONException {
        int index = location.lastIndexOf(".");
        JSONObject current = original;
        String key = location;

        if (index != -1) {
            current = getOrCreatePath(original, location.substring(0, index));
            key = location.substring(index + 1);
        }

        current.put(key, value);
    }


    public static Object parseObject(JSONObject object) {
        if (object.has("pn_val")) {
            return object.opt("pn_val");
        } else if (SyncedObject.isPnList(object)) {
            return objectToSortedArray(object);
        } else {
            return objectToHashMap(object);
        }
    }

    public static HashMap objectToHashMap(JSONObject object) {
        HashMap result = new HashMap();
        Iterator objectIterator = object.keys();
        String currentKey;

        while (objectIterator.hasNext()) {
            currentKey = (String) objectIterator.next();
            result.put(currentKey, parseObject((JSONObject) object.opt(currentKey)));
        }

        return result;
    }

    public static ArrayList objectToSortedArray(JSONObject object) {
        Iterator iterator = object.sortedKeys();
        ArrayList result = new ArrayList();
        JSONObject current;

        while (iterator.hasNext()) {
            current = (JSONObject) object.opt((String) iterator.next());

            if (current.has("pn_val")) {
                result.add(current.opt("pn_val"));
            } else if (SyncedObject.isPnList(current)) {
                result.add(objectToSortedArray(current));
            } else {
                result.add(objectToHashMap(current));
            }
        }

        return result;
    }
}
