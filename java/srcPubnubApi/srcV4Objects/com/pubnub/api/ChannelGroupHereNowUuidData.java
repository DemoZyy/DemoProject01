package com.pubnub.api;

import org.json.JSONObject;

public class ChannelGroupHereNowUuidData {
    String uuid;
    JSONObject metadata;
    public static String arrayToString(ChannelGroupHereNowUuidData[] uuids) {
        String s = "";
        
        for (ChannelGroupHereNowUuidData h : uuids) {
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
