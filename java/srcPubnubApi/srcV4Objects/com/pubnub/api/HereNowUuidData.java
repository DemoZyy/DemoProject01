package com.pubnub.api;

import org.json.JSONObject;

public class HereNowUuidData {
    String uuid;
    JSONObject metadata;
    public static String arrayToString(HereNowUuidData[] uuids) {
        String s = "";
        
        for (HereNowUuidData h : uuids) {
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
