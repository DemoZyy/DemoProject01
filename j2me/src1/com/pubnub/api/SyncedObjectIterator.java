package com.pubnub.api;

import java.util.Enumeration;
import java.util.Iterator;

public class SyncedObjectIterator implements Iterator {

    Enumeration jsonObjectEnumeration;
    SyncedObject syncedObject;

    public SyncedObjectIterator(SyncedObject syncedObject, Object jsonObjectIterator) {
        this.syncedObject = syncedObject;
        this.jsonObjectEnumeration = (Enumeration) jsonObjectIterator;
    }

    public boolean hasNext() {
        return this.jsonObjectEnumeration.hasMoreElements();
    }

    public Object next() {
        return this.syncedObject.child((String) this.jsonObjectEnumeration.nextElement());
    }
}
