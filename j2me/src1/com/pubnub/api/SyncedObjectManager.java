package com.pubnub.api;

import java.util.Enumeration;
import java.util.HashMap;
import org.json.me.*;

public class SyncedObjectManager extends SyncedObjectManagerCore {
    public SyncedObjectManager(PubnubCore pubnub) {
        super(pubnub);
    }

    SyncedObject buildSyncedObject(String objectID, String path) {
        return new SyncedObject(this, objectID, path);
    }

    public static HashMap objectToHashMap(JSONObject object) {
        HashMap result = new HashMap();
        Enumeration objectIterator = (Enumeration) object.keys();
        String currentKey;

        while (objectIterator.hasMoreElements()) {
            currentKey = (String) objectIterator.nextElement();
            result.put(currentKey, parseObject((JSONObject) object.opt(currentKey)));
        }

        return result;
    }
}
