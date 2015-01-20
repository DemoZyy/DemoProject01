package com.pubnub.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class SyncedObjectDelta {

    private String action;
    private String updatedAt;
    private String location;
    private Long timetoken;
    private UUID transID;
    private Object value;

    private static Logger log = new Logger(SyncedObjectDelta.class);

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
            log.verbose("Error parsing DS delta at " + location + ". Message: " + updateJSON.toString());
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
