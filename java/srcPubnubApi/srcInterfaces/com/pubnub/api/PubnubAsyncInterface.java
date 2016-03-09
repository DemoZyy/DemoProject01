package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONObject;

interface PubnubAsyncInterface {
    

    public PubnubSubscribe subscribe();
    
    public PubnubPublishAsync publish();
    
    


    /**
     * Disconnect from all channels, and resubscribe
     */
    public void disconnectAndResubscribe();

    /**
     * Disconnect from all channels, and resubscribe
     */
    public void disconnectAndResubscribe(PubnubError error);

    /**
     * Disconnect from all channels, and resubscribe
     */
    public void disconnectAndResubscribeWithTimetoken(String timetoken);

    /**
     * Disconnect from all channels, and resubscribe
     */
    public void disconnectAndResubscribeWithTimetoken(String timetoken, PubnubError error);

    /**
     * Get Cache Busting value
     *
     * @return current cache busting setting
     */
    public boolean getCacheBusting();

    /**
     * This method returns all channel names currently subscribed to in form of
     * a comma separated String
     *
     * @return Comma separated string with all channel names currently
     *         subscribed
     */
    public String getCurrentlySubscribedChannelNames();

    /**
     * Returns presence heartbeat value
     *
     * @return Current presence heartbeat value
     */
    public int getHeartbeat();

    public int getHeartbeatInterval();

    /**
     * Returns current max retries for Subscribe
     *
     * @return Current max retries
     */
    public int getMaxRetries();

    /**
     * Returns presence expiry timeout value
     *
     * @return Current presence expiry timeout value
     */
    public int getPnExpires();

    /**
     * Returns Resume on Reconnect current setting
     *
     * @return Resume on Reconnect setting
     */
    public boolean getResumeOnReconnect();

    /**
     * Returns current retry interval for subscribe
     *
     * @return Current Retry Interval in milliseconds
     */
    public int getRetryInterval();

    public void getState(String channel, String uuid, Callback callback);

    /**
     * This method returns array of channel names, currently subscribed to
     *
     * @return Array of channel names
     */
    public String[] getSubscribedChannelsArray();

    /**
     * Returns current window interval for subscribe
     *
     * @return Current Window Interval in milliseconds
     */
    public int getWindowInterval();

    

    /**
     * This method returns the state of Resume on Reconnect setting
     *
     * @return Current state of Resume On Reconnect Setting
     */
    public boolean isResumeOnReconnect();


    /**
     * Enable/Disable Cache Busting
     *
     * @param cacheBusting
     */
    public void setCacheBusting(boolean cacheBusting);

    public void setHeartbeat(int heartbeat);

    /**
     * This method sets presence expiry timeout.
     *
     * @param heartbeat
     *            Presence Heartbeat value in seconds
     */
    public void setHeartbeat(int heartbeat, Callback callback);

    /**
     *
     * @param heartbeatInterval
     */
    public void setHeartbeatInterval(int heartbeatInterval);

    /**
     *
     * @param heartbeatInterval
     * @param callback
     */
    public void setHeartbeatInterval(int heartbeatInterval, Callback callback);

    /**
     * This methods sets maximum number of retries for subscribe. Pubnub API
     * will make maxRetries attempts to connect to pubnub servers before timing
     * out.
     *
     * @param maxRetries
     *            Max number of retries
     */
    public void setMaxRetries(int maxRetries);

    /**
     *
     * @param pnexpires
     */
    public void setPnExpires(int pnexpires);

    /**
     * This method sets presence expiry timeout.
     *
     * @param pnexpires
     *            Presence Expiry timeout in seconds
     */
    public void setPnExpires(int pnexpires, Callback callback);

    /**
     * If Resume on Reconnect is set to true, then Pubnub catches up on
     * reconnection after disconnection. If false, then messages sent on the
     * channel between disconnection and reconnection are not received.
     *
     * @param resumeOnReconnect
     *            True or False setting for Resume on Reconnect
     */
    public void setResumeOnReconnect(boolean resumeOnReconnect);

    /**
     * This method sets retry interval for subscribe. Pubnub API will make
     * maxRetries attempts to connect to pubnub servers. These attemtps will be
     * made at an interval of retryInterval milliseconds.
     *
     * @param retryInterval
     *            Retry Interval in milliseconds
     */
    public void setRetryInterval(int retryInterval);

    /**
     *
     * @param channel
     * @param uuid
     * @param state
     * @param callback
     */
    public void setState(String channel, String uuid, JSONObject state, Callback callback);

    /**
     * This method sets window interval for subscribe.
     *
     * @param windowInterval
     *            Window Interval in milliseconds
     */
    public void setWindowInterval(int windowInterval);

    /**
     * This method sets timeout value for subscribe/presence. Default value is
     * 310000 milliseconds i.e. 310 seconds
     *
     * @param timeout
     *            Timeout value in milliseconds for subscribe/presence
     */
    public void setSubscribeTimeout(int timeout);

    /**
     * This method set timeout value for non subscribe operations like publish,
     * history, hereNow. Default value is 15000 milliseconds i.e. 15 seconds.
     *
     * @param timeout
     *            Timeout value in milliseconds for Non subscribe operations
     *            like publish, history, hereNow
     */
    public void setNonSubscribeTimeout(int timeout);

    /**
     * This method when called stops Pubnub threads
     */
    public void shutdown();

    
    
    
    /**
     * Read current time from PubNub Cloud.
     *
     * @param callback
     *            Callback object
     */
    public void time(Callback callback);

    /**
     * Unsubscribe from channels.
     *
     * @param channels
     *            String array containing channel names
     */
    public void unsubscribe(String[] channels, Callback callback);

    /**
     * Unsubscribe from channels.
     *
     * @param channels
     *            String array containing channel names
     */
    public void unsubscribe(String[] channels);

    /**
     * Unsubscribe/Disconnect from channel.
     *
     * @param channel
     *            channel name as String.
     */
    public void unsubscribe(String channel);

    /**
     * Unsubscribe/Disconnect from channel.
     *
     * @param channel
     *            channel name as String.
     */
    public void unsubscribe(String channel, Callback callback);

    /**
     * Unsubscribe from channel group
     *
     * @param group
     *            to unsubscribe
     */
    public void channelGroupUnsubscribe(String group);

    /**
     * Unsubscribe from channel group
     *
     * @param group
     *            to unsubscribe
     * @param callback
     *            Callback
     */
    public void channelGroupUnsubscribe(String group, Callback callback);

    /**
     * Unsubscribe from multiple channel groups
     *
     * @param groups
     *            to unsubscribe
     * @param callback
     *            Callback
     */
    public void channelGroupUnsubscribe(String[] groups, Callback callback);

    /**
     * Unsubscribe from multiple channel groups
     *
     * @param groups
     *            to unsubscribe
     */
    public void channelGroupUnsubscribe(String[] groups);

    /**
     * Unsubscribe from presence channel.
     *
     * @param channel
     *            channel name as String.
     * @param callback
     *            Callback
     */
    public void unsubscribePresence(String channel, Callback callback);

    /**
     * Unsubscribe from presence channel.
     *
     * @param channel
     *            channel name as String.
     */
    public void unsubscribePresence(String channel);

    /**
     * Unsubscribe from all channels and channel groups.
     * 
     * @param callback
     */
    public void unsubscribeAll(Callback callback);

    /**
     * Unsubscribe from all channels and channel groups.
     */
    public void unsubscribeAll();

    /**
     * Unsubscribe from all channels.
     */
    public void unsubscribeAllChannels();

    /**
     * Unsubscribe from all channels.
     * 
     * @param callback
     *            Callback
     */
    public void unsubscribeAllChannels(Callback callback);

    /**
     * Unsubscribe from all channel groups.
     * 
     * @param callback
     *            Callback
     */
    public void channelGroupUnsubscribeAllGroups(Callback callback);



}
