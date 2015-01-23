package com.pubnub.api;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONHelper extends JSONHelperCore {
    /**
     * Merge original and newObject. Does not support JSONArrays.
     *
     * @param original object
     * @param newObject object
     * @throws JSONException
     */
    public static JSONObject merge(JSONObject original, JSONObject newObject) throws JSONException {
        return merge(original, newObject, null);
    }

    /**
     * Merge original and newObject at specified location. Does not support JSONArrays.
     *
     * @param original object
     * @param newObject object
     * @param location path
     * @throws JSONException
     */
    public static JSONObject merge(JSONObject original, JSONObject newObject, String location) throws JSONException {
        JSONObject current;

        if (PubnubUtil.isBlank(location)) {
            current = original;
        } else {
            String[] pathElements = PubnubUtil.splitString(location, ".");
            current = original;
            int length = pathElements.length;

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < length; i++) {
                String key = pathElements[i];

                if (!current.has(key)) {
                    current.put(key, new JSONObject());
                }

                current = current.getJSONObject(key);
            }
        }

        Iterator fieldNames = newObject.keys();
        Object newNode;
        Object originalNode;

        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();
            newNode = newObject.get(fieldName);

            try {
                originalNode = current.get(fieldName);
            } catch (JSONException e) {
                originalNode = null;
            }

            if (originalNode instanceof JSONObject && newNode instanceof JSONObject) {
                current.put(fieldName, merge((JSONObject) originalNode, (JSONObject) newNode));
            } else {
                current.put(fieldName, newNode);
            }
        }

        return original;
    }

}
