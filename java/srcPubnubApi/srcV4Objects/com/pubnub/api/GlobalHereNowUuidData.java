package com.pubnub.api;

import org.json.JSONObject;

public class GlobalHereNowUuidData {
    String uuid;
    JSONObject metadata;
    public static String arrayToString(GlobalHereNowUuidData[] uuids) {
        String s = "";
        
        for (GlobalHereNowUuidData h : uuids) {
            s += h.toString() + "\n";
        }
        return s;
    }
    
    public String toString() {
        String s = "";
        s += uuid;
        if (metadata != null) s += (" : " + metadata.toString());
        return s;
    }
}
