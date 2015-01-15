package com.pubnub.api;

import org.json.JSONObject;

import java.util.Iterator;

public class PubnubUtil extends PubnubUtilShared {
    public static Iterator jsonObjectKeysSortedIterator(JSONObject jsonObject) {
        return jsonObject.sortedKeys();
    }
}
