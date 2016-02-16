package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;

public class HistoryData {
    public JSONArray getMessages() {
        return messages;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    JSONArray messages;
    String start;
    String end;
    
    public String toString() {
        String s = "";
        s = s + "Start: " + start + "\n";
        s = s + "End: " + start + "\n";
        try {
            s = s + "Messages: " + messages.toString(2) + "\n";
        } catch (JSONException e) {
           // ERROR
        }
        return s;
    }
    
}
