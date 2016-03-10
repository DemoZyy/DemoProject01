package com.pubnub.api;

import org.json.JSONObject;

import java.util.UUID;

/**
 * PubnubSync object facilitates querying channels for messages and listening on
 * channels for presence/message events
 *
 * @author Pubnub
 *
 */
public class PubnubSync extends PubnubCoreSync {

    public PubnubSync() {
        
    }

    protected String getUserAgent() {
        return "Java-Sync/" + VERSION;
    }

    /**
     * Sets value for UUID
     *
     * @param uuid
     *            UUID value for Pubnub client
     */
    public void setUUID(UUID uuid) {
        this.UUID = uuid.toString();
    }

    public String uuid() {
        return java.util.UUID.randomUUID().toString();
    }

}
