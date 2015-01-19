package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

abstract public class SyncedObjectManagerCore {
    private Pubnub pubnub;
    private HashMap syncedObjects;
    private JSONObject data;
    private HashMap callbacks;
    private HashMap updates;
    private HashSet objectsSyncPending;

    protected SubscribeManager dataSyncManager;
    private volatile String _timetoken = "0";
    private volatile String _saved_timetoken = "0";
    private volatile boolean resumeOnReconnect;

    private final static String DS_SUBSCRIBE_RESPONSE_ACTION = "action";
    private final static String DS_SUBSCRIBE_RESPONSE_STATUS = "status";
    private final static String DS_SUBSCRIBE_STATUS_COMPLETE = "complete";

    public final static String ACTION_MERGE = "merge";
    public final static String ACTION_REPLACE = "replace";
    public final static String ACTION_REPLACE_DELETE = "replace-delete";
    public final static String ACTION_PUSH = "push";
    public final static String ACTION_REMOVE = "delete";

    private static Logger log = new Logger(SyncedObjectManagerCore.class);

    public SyncedObjectManagerCore(PubnubCore pubnub) {
        this.pubnub = (Pubnub) pubnub;
        this.syncedObjects = new HashMap();
        this.data = new JSONObject();
        this.callbacks = new HashMap();
        this.updates = new HashMap();
        this.objectsSyncPending = new HashSet();

        this.dataSyncManager = new SubscribeManager("DataSync-Manager-"
                + System.identityHashCode(this), 10000, 310000);

        this.dataSyncManager.setHeader("V", PubnubCore.VERSION);
        this.dataSyncManager.setHeader("Accept-Encoding", "gzip");
        this.dataSyncManager.setHeader("User-Agent", pubnub.getUserAgent());
    }

    public SyncedObject add(String objectID, String path, DataSyncCallback callback) {
        String location = SyncedObject.glue(objectID, path);
        SyncedObject object;
        SyncedObject parentObject;

        synchronized (this.syncedObjects) {
            parentObject = getAlreadySubscribedObjectIfExists(location);

            if (callback != null) {
                this.callbacks.put(location, callback);
            }

            object = buildSyncedObject(objectID, path);

            this.syncedObjects.put(object.getLocation(), object);
        }

        if (parentObject == null) {
            this.objectsSyncPending.add(location);
            resubscribe(true, false, null);
        } else if (callback != null) {
            object.setIsConnected(true);
            callback.connectCallback(location);
            object.setIsReady(true);
            callback.invokeReadyCallback(object);
        }

        return object;
    }


    abstract SyncedObject buildSyncedObject(String objectID, String path);

    public Pubnub getPubnub() {
        return pubnub;
    }

    private synchronized SyncedObject getAlreadySubscribedObjectIfExists(String location) {
        synchronized (this.syncedObjects) {
            if (this.syncedObjects.containsKey(location)) {
                return (SyncedObject) this.syncedObjects.get(location);
            }

            Iterator chs = this.syncedObjects.entrySet().iterator();
            Map.Entry entry;

            while (chs.hasNext()) {
                entry = (Map.Entry) chs.next();
                if (location.indexOf(entry.getKey() + ".") == 0) {
                    return (SyncedObject) entry.getValue();
                }
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

    protected Object getValue(String location) throws JSONException {
        return parseObject(getRawValue(location));
    }

    protected String firstListKey(String location) throws JSONException {
        JSONObject value = getRawValue(location);
        String result;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (value) {
            result = (String) PubnubUtil.jsonObjectKeysSortedIterator(value).next();
        }

        return  result;
    }

    protected String lastListKey(String location) throws JSONException {
        JSONObject value = getRawValue(location);
        String lastKey = null;

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (value) {
            Iterator valueKeys = PubnubUtil.jsonObjectKeysSortedIterator(value);

            while (valueKeys.hasNext()) {
                lastKey = (String) valueKeys.next();
            }
        }

        return lastKey;
    }

    private void invokeActionCallbacks(ArrayList cbs, String action, String updatedAt, List data) {
        Iterator cbsIterator = cbs.iterator();

        //noinspection WhileLoopReplaceableByForEach
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

    private ArrayList getCallbacksByLocation(String location) {
        return getCallbacksByLocation(location, false);
    }

    private ArrayList getCallbacksByLocation(String location, boolean strict) {
        Iterator keys = callbacks.entrySet().iterator();
        ArrayList resultCallbacks = new ArrayList();
        String currentLocation;

        while (keys.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) keys.next();
            currentLocation = (String) entry.getKey();
            if (currentLocation != null) {
                if ((strict && location.equals(currentLocation))
                        || (!strict && location.indexOf(currentLocation) == 0)) {
                    resultCallbacks.add(callbacks.get(currentLocation));
                }
            }
        }

        return resultCallbacks;
    }

    private String[] getChannelsForSubscribe() {
        String[] result;

        synchronized (this.syncedObjects) {
            HashSet channelsSet = new HashSet();
            Iterator channelsIterator = this.syncedObjects.entrySet().iterator();
            Map.Entry entry;
            String channel;
            SyncedObject syncedObject;

            while (channelsIterator.hasNext()) {
                entry = (Map.Entry) channelsIterator.next();
                channel = (String) entry.getKey();
                syncedObject = (SyncedObject) entry.getValue();

                if (!syncedObject.getIsUnsubscribed()) {
                    channelsSet.add("pn_ds_" + channel);
                    channelsSet.add("pn_ds_" + channel + ".*");
                    channelsSet.add("pn_dstr_" + PubnubUtil.splitString(channel, ".")[0]);
                }
            }

            result = new String[channelsSet.size()];
            channelsSet.toArray(result);
        }

        return result;
    }

    private String[] getChannelsForUnsubscribe(String objectId) {
        return new String[]{
                "pn_ds_" + objectId,
                "pn_ds_" + objectId + ".*",
                "pn_dstr_" + PubnubUtil.splitString(objectId, ".")[0]
        };
    }

    // TODO: refactor HACK!
    private boolean isResumeOnReconnect() {
        return resumeOnReconnect;
    }

    private void resubscribe() {
        if (!_timetoken.equals("0"))
            _saved_timetoken = _timetoken;
        _timetoken = "0";
        log.verbose("Before DS Resubscribe Timetoken : " + _timetoken);
        log.verbose("Before DS Resubscribe Saved Timetoken : " + _saved_timetoken);
        resubscribe(true, true);
    }

    private void resubscribe(boolean fresh, boolean dar) {
        resubscribe(fresh, dar, null);
    }

    private void resubscribe(boolean fresh, boolean dar, Worker worker) {
        String[] channelsArray = getChannelsForSubscribe();
        String channelString = PubnubUtil.joinString(channelsArray, ",");

        if (channelString.isEmpty()) {
            dataSyncManager.resetHttpManager();
        }

        String[] urlComponents = {pubnub.getPubnubUrl(), "subscribe",
                pubnub.SUBSCRIBE_KEY,
                channelString, "0", _timetoken
        };

        Hashtable params = PubnubUtil.hashtableClone(pubnub.getParams());
        params.put("uuid", pubnub.getUUID());

        HttpRequest hreq = new HttpRequest(urlComponents, params, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                JSONArray jsa;
                try {
                    jsa = new JSONArray(response);

                    _timetoken = (!_saved_timetoken.equals("0") && isResumeOnReconnect())
                            ? _saved_timetoken
                            : jsa.get(1).toString();

                    log.verbose("Resume On Reconnect is " + isResumeOnReconnect());
                    log.verbose("Saved Timetoken : " + _saved_timetoken);
                    log.verbose("In Response Timetoken : " + jsa.get(1).toString());
                    log.verbose("Timetoken value set to " + _timetoken);
                    _saved_timetoken = "0";
                    log.verbose("Saved Timetoken reset to 0");

                    if (!hreq.isDar()) {
                        invokeConnectCallback();
                    } else {
                        invokeReconnectCallback();
                    }

                    JSONArray messages = new JSONArray(jsa.get(0).toString());

                    if (jsa.length() == 4) {
                        String channelsString = jsa.getString(3);
                        String[] channels;

                        if (channelsString.length() == 0) {
                            channels = new String[]{};
                        } else {
                            channels = PubnubUtil.splitString(channelsString, ",");
                        }

                        for (int i = 0; i < channels.length; i++) {
                            if (channels[i].indexOf("pn_ds") == 0 && channels[i].indexOf("*") == -1) {
                                handleMessage(channels[i], messages.get(i));
                            }
                        }
                    }

                    if (hreq.isSubzero()) {
                        log.verbose("Response of subscribe 0 request. Need to do dAr process again");
                        resubscribe(false, hreq.isDar(), hreq.getWorker());
                    } else
                        resubscribe(false, false, null);
                } catch (JSONException e) {
                    if (hreq.isSubzero()) {
                        log.verbose("Response of subscribe 0 request. Need to do dAr process again");
                        resubscribe(false, hreq.isDar(), hreq.getWorker());
                    } else
                        resubscribe(false, false, hreq.getWorker());
                }
            }

            public void handleBackFromDar(HttpRequest hreq) {
                resubscribe(false, false, hreq.getWorker());
            }

            public void handleTimeout(HttpRequest hreq) {
                log.verbose("Timeout Occurred, Calling disconnect callbacks on the channels");
                String timeoutTimetoken = (isResumeOnReconnect()) ?
                        (_timetoken.equals("0")) ? _saved_timetoken : _timetoken
                        : "0";
                log.verbose("Timeout Timetoken : " + timeoutTimetoken);
                invokeDisconnectCallback();
                invokeErrorCallback(PubnubError.getErrorObject(PubnubError.PNERROBJ_TIMEOUT, 1));
            }

            public String getTimetoken() {
                return _timetoken;
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                log.verbose("Received disconnectAndResubscribe");
                invokeErrorCallback(error);
                resubscribe();
            }
        });

        if (_timetoken.equals("0")) {
            hreq.setSubzero(true);
            log.verbose("This is a subscribe 0 request");
        }

        hreq.setDar(dar);

        if (worker != null)
            hreq.setWorker(worker);

        if (fresh) {
            dataSyncManager.resetHttpManager();
        }

        dataSyncManager.queue(hreq);
    }

    private void handleMessage(String channel, Object data) {
        JSONObject message = (JSONObject) data;

        if (message.has(DS_SUBSCRIBE_RESPONSE_ACTION)) {
            SyncedObjectDelta update = new SyncedObjectDelta(message, channel);
            SyncedObjectUpdatesList list;
            String transaction = update.getTransID().toString();

            synchronized (this.updates) {
                if (!this.updates.containsKey(transaction)) {
                    list = new SyncedObjectUpdatesList();
                    this.updates.put(transaction, list);
                } else {
                    list = (SyncedObjectUpdatesList) this.updates.get(transaction);
                }

                list.add(update);
            }
        } else if (message.has(DS_SUBSCRIBE_RESPONSE_STATUS)) {
            String status;
            String transaction;

            try {
                status = message.getString(DS_SUBSCRIBE_RESPONSE_STATUS);
            } catch (JSONException e) {
                invokeErrorCallback(PubnubError.getErrorObject(PubnubError.PNERROBJ_JSON_ERROR, message.toString()));
                return;
            }

            if (!status.equals(DS_SUBSCRIBE_STATUS_COMPLETE)) return;

            try {
                transaction = message.getString("trans_id");
            } catch (JSONException e) {
                invokeErrorCallback(PubnubError.getErrorObject(PubnubError.PNERROBJ_JSON_ERROR, message.toString()));
                return;
            }

            synchronized (this.updates) {
                SyncedObjectUpdatesList updatesList =
                        (SyncedObjectUpdatesList) this.updates.get(transaction);

                if (updatesList == null || updatesList.size() == 0) {
                    invokeErrorCallback(PubnubError.getErrorObject(
                            PubnubError.PNERROBJ_DATA_SYNC_NO_UPDATES_FOR_TRANSACTION,
                            transaction
                    ));
                    return;
                }

                updatesList.setComplete(true);

                SyncedObjectDelta first = (SyncedObjectDelta) updatesList.get(0);

                String objectId = PubnubUtil.splitString(first.getLocation(), ".")[0];

                if (isObjectSyncPending(objectId)) {
                    applyUpdates(updatesList);
                    this.updates.remove(transaction);
                }
            }
        }
    }

    /**
     * Invoke connect callback on not connected objects
     */
    private void invokeConnectCallback() {
        HashSet objectsToFetch = new HashSet();
        Map.Entry value;
        String key;
        SyncedObject syncedObject;
        DataSyncCallback dataSyncCallback;

        synchronized (this.syncedObjects) {
            Iterator valuesIterator = this.syncedObjects.entrySet().iterator();

            while (valuesIterator.hasNext()) {
                value = (Map.Entry) valuesIterator.next();
                key = (String) value.getKey();
                syncedObject = (SyncedObject) value.getValue();

                if (!syncedObject.getIsConnected()) {
                    dataSyncCallback = (DataSyncCallback) this.callbacks.get(key);
                    if (dataSyncCallback != null) {
                        dataSyncCallback.connectCallback(syncedObject.getLocation());
                        syncedObject.setIsConnected(true);
                        objectsToFetch.add(syncedObject.getLocation());
                    }
                }
            }
        }

        Iterator objectsToFetchIterator = objectsToFetch.iterator();

        while (objectsToFetchIterator.hasNext()) {
            key = (String) objectsToFetchIterator.next();
            syncedObject = (SyncedObject) this.syncedObjects.get(key);
            dataSyncCallback = (DataSyncCallback) this.callbacks.get(key);

            fetchObject(syncedObject.getObjectID(), syncedObject.getPath(), dataSyncCallback);
        }
    }

    private void invokeReconnectCallback() {
        Map.Entry value;
        String key;
        SyncedObject syncedObject;
        DataSyncCallback dataSyncCallback;

        synchronized (this.syncedObjects) {
            Iterator valuesIterator = this.syncedObjects.entrySet().iterator();

            while (valuesIterator.hasNext()) {
                value = (Map.Entry) valuesIterator.next();
                key = (String) value.getKey();
                syncedObject = (SyncedObject) value.getValue();

                if (syncedObject.getIsConnected()) {
                    dataSyncCallback = (DataSyncCallback) this.callbacks.get(key);
                    if (dataSyncCallback != null) {
                        dataSyncCallback.reconnectCallback();
                    }
                }
            }
        }
    }

    private void invokeErrorCallback(PubnubError error) {
        invokeErrorCallback(error, new ArrayList(this.callbacks.values()));
    }

    private void invokeErrorCallback(String location, PubnubError error) {
        invokeErrorCallback(error, getCallbacksByLocation(location));
    }

    private void invokeErrorCallback(PubnubError error, ArrayList cbs) {
        synchronized (this.callbacks) {
            Iterator cbsIterator = cbs.iterator();

            while (cbsIterator.hasNext()) {
                ((DataSyncCallback) cbsIterator.next()).errorCallback(error);
            }
        }
    }

    private void invokeDisconnectCallback() {
        synchronized (this.syncedObjects) {
            Iterator objectsIterator = syncedObjects.values().iterator();
            SyncedObject syncedObject;
            DataSyncCallback dataSyncCallback;

            while (objectsIterator.hasNext()) {
                syncedObject = (SyncedObject) objectsIterator.next();
                syncedObject.setIsConnected(false);
                dataSyncCallback = (DataSyncCallback) this.callbacks.get(syncedObject.getLocation());
                if (dataSyncCallback != null) {
                    dataSyncCallback.disconnectCallback();
                }
            }
        }
    }

    private void applyAllUpdates() throws JSONException {
        // DANGER: complete transactions of not ready objects can be applied
        synchronized (this.updates) {
            Iterator updatesIterator = this.updates.keySet().iterator();
            String key;

            while (updatesIterator.hasNext()) {
                key = (String) updatesIterator.next();

                applyUpdates((SyncedObjectUpdatesList) this.updates.get(key));
            }
        }
    }

    private void applyUpdates(SyncedObjectUpdatesList updatesList) {
        if (!updatesList.isComplete()) {
            return;
        }

        Iterator updatesIterator = updatesList.iterator();
        SyncedObjectDelta currentDelta = null;

        // apply updates on object
        while (updatesIterator.hasNext()) {
            try {
                currentDelta = (SyncedObjectDelta) updatesIterator.next();
                applyUpdate(currentDelta);
            } catch (JSONException e) {
                invokeErrorCallback(currentDelta.getLocation(), PubnubError.PNERROBJ_JSON_ERROR);
            }
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

    private synchronized void applyUpdate(SyncedObjectDelta delta) throws JSONException {
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

    private boolean isObjectSyncPending(String objectId) {
        return this.objectsSyncPending.contains(objectId);
    }

    private void fetchObject(String objectId, String path, final DataSyncCallback callback) {
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

    protected void remove(String location) {
        synchronized (this.syncedObjects) {
            this.data.remove(location);
            unsubscribe(location);
            this.syncedObjects.remove(location);
        }
    }

    protected void unsubscribe(String location) {
        this.callbacks.remove(location);
        this.objectsSyncPending.remove(location);

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
            return SyncedObjectManager.objectToHashMap(object);
        }
    }

    public static ArrayList objectToSortedArray(JSONObject object) {
        Iterator iterator = PubnubUtil.jsonObjectKeysSortedIterator(object);
        ArrayList result = new ArrayList();
        JSONObject current;

        while (iterator.hasNext()) {
            current = (JSONObject) object.opt((String) iterator.next());

            if (current.has("pn_val")) {
                result.add(current.opt("pn_val"));
            } else if (SyncedObject.isPnList(current)) {
                result.add(objectToSortedArray(current));
            } else {
                result.add(SyncedObjectManager.objectToHashMap(current));
            }
        }

        return result;
    }
}
