package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONHelperCore {
    /**
     * Get or create json path in original JSONObject.
     *
     * @param original object
     * @param path to return
     * @return JSONObject located at path.
     */
    public static JSONObject getOrCreateObjectAtPath(JSONObject original, String path)
            throws JSONException {
        if (PubnubUtil.isBlank(path)) {
            return original;
        }

        String[] pathElements = PubnubUtil.splitString(path, ".");
        JSONObject current = original;
        int length = pathElements.length;

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < length; i++) {
            String key = pathElements[i];

            if (!current.has(key)) {
                current.put(key, new JSONObject());
            }

            current = current.getJSONObject(key);
        }

        return current;
    }

    /**
     * Update leaf node
     *
     * @param original object
     * @param value object
     * @param location of value object
     * @throws JSONException
     */
    public static void updateJSONObjectValue(JSONObject original, Object value, String location)
            throws JSONException {
        int index = location.lastIndexOf(".");
        JSONObject current = original;
        String key = location;

        if (index != -1) {
            current = getOrCreateObjectAtPath(original, location.substring(0, index));
            key = location.substring(index + 1);
        }

        current.put(key, value);
    }
}
