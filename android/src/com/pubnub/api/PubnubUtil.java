package com.pubnub.api;

import java.util.Iterator;
import java.util.TreeSet;
import org.json.JSONObject;

public class PubnubUtil extends PubnubUtilShared {
    public static Iterator jsonObjectKeysSortedIterator(JSONObject jsonObject) {
        Iterator keysIterator = jsonObject.keys();
        TreeSet set = new TreeSet();

        while (keysIterator.hasNext()) {
            set.add(keysIterator.next());
        }

        return set.iterator();
    }
}
