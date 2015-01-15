package com.pubnub.api;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class SyncedObjectManager extends SyncedObjectManagerCore {
    public SyncedObjectManager(PubnubCore pubnub) {
        super(pubnub);
    }

    SyncedObject buildSyncedObject(String objectID, String path) {
        return new SyncedObject(this, objectID, path);
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
}
