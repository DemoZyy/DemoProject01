package com.pubnub.api;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class OriginsPool {
    private LinkedHashSet<String> originsList;

    public synchronized void set(Set<String> originsList) throws PubnubException {
        if (originsList.size() < 2) {
            throw new PubnubException("It should be at least 2 origins in Origins Pool");
        }

        this.originsList = new LinkedHashSet<String>(originsList);
    }

    protected synchronized void add(String origin) {
        LinkedHashSet<String> newList = new LinkedHashSet<String>();

        newList.add(origin);
        newList.addAll(this.originsList);

        this.originsList = newList;
    }

    protected synchronized void remove(String origin) {
        originsList.remove(origin);
    }

    public synchronized String first() {
        Iterator<String> iterator = originsList.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            throw new NoSuchElementException("empty ");
        }
    }

    public synchronized int size() {
        return originsList.size();
    }

    public synchronized boolean contains(String value) {
        return originsList.contains(value);
    }

    public synchronized Iterator<String> iterator() {
        return originsList.iterator();
    }
}
