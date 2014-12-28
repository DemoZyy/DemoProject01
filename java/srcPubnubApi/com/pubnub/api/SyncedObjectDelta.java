package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

// TODO: store original data type
public class SyncedObjectDelta {

    private String action;
    private String updatedAt;
    private String location;
    private Long timetoken;
    private UUID transID;
    private Object value;

    public SyncedObjectDelta(JSONObject updateJSON, String location) {
        try {
            action = updateJSON.getString("action");
            updatedAt = updateJSON.getString("updateAt");
            this.location = location.substring(6);
            timetoken = new Long(updateJSON.getLong("timetoken"));
            transID = UUID.fromString(updateJSON.getString("trans_id"));

            if (updateJSON.has("value")) {
                this.value = updateJSON.get("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO: all fields should exist
        }
    }

    public String getAction() {
        return action;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Long getTimetoken() {
        return timetoken;
    }

    public UUID getTransID() {
        return transID;
    }

    public Object getValue() {
        return value;
    }

    public String getLocation() {
        return location;
    }
}
