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
public class PubnubSync extends PubnubCoreSync implements PubnubSyncInterfacePam, PubnubSyncInterfacePush {

    public PubnubSync() {
        
    }


    @Override
    public Object enablePushNotificationsOnChannel(String channel, String gcmRegistrationId) {
        return _enablePushNotificationsOnChannels(new String[] { channel }, gcmRegistrationId, null, true);
    }

    @Override
    public Object enablePushNotificationsOnChannels(String[] channels, String gcmRegistrationId) {
        return _enablePushNotificationsOnChannels(channels, gcmRegistrationId, null, true);
    }

    @Override
    public Object disablePushNotificationsOnChannel(String channel, String gcmRegistrationId) {
        return _disablePushNotificationsOnChannels(new String[] { channel }, gcmRegistrationId, null, true);
    }

    @Override
    public Object disablePushNotificationsOnChannels(String[] channels, String gcmRegistrationId) {
        return _disablePushNotificationsOnChannels(channels, gcmRegistrationId, null, true);
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
