package com.pubnub.api;

import java.util.Iterator;

public class SyncedObjectIterator implements Iterator {

    Iterator jsonObjectIterator;
    SyncedObject syncedObject;

    public SyncedObjectIterator(SyncedObject syncedObject, Object jsonObjectIterator) {
        this.syncedObject = syncedObject;
        this.jsonObjectIterator = (Iterator) jsonObjectIterator;
    }

    @Override
    public boolean hasNext() {
        return this.jsonObjectIterator.hasNext();
    }

    @Override
    public Object next() {
        return this.syncedObject.child((String) this.jsonObjectIterator.next());
    }
}
