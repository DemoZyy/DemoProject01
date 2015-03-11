package com.pubnub.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.pubnub.api.PubnubError.PNERROBJ_SECRET_KEY_MISSING;
import static com.pubnub.api.PubnubError.getErrorObject;

/**
 * Pubnub object facilitates querying channels for messages and listening on
 * channels for presence/message events
 *
 * @author Pubnub
 *
 */

abstract class PubnubCoreShared extends PubnubCore {

    private boolean originManagerExplicitlyEnabled;
    private int originHeartbeatInterval;
    private int originHeartbeatIntervalAfterFailure;
    private int originHeartbeatMaxRetries;
    private Set<String> originsPool;
    private OriginManager originManager;

    /**
     * Pubnub Constructor
     *
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     * @param secret_key
     *            Secret Key
     * @param cipher_key
     *            Cipher Key
     * @param ssl_on
     *            SSL on ?
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, String secret_key,
                  String cipher_key, boolean ssl_on) {
        super(publish_key, subscribe_key, secret_key, cipher_key, ssl_on);
    }

    /**
     * Pubnub Constructor
     *
     * @param publish_key
     *            Publish key
     * @param subscribe_key
     *            Subscribe Key
     * @param secret_key
     *            Secret Key
     * @param ssl_on
     *            SSL on ?
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, String secret_key,
                  boolean ssl_on) {
        super(publish_key, subscribe_key, secret_key, "", ssl_on);
    }

    /**
     * Pubnub Constructor
     *
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     */
    public PubnubCoreShared(String publish_key, String subscribe_key) {
        super(publish_key, subscribe_key, "", "", false);
    }

    /**
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     * @param ssl
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, boolean ssl) {
        super(publish_key, subscribe_key, "", "", ssl);
    }

    /**
     * @param publish_key
     * @param subscribe_key
     * @param secret_key
     */
    public PubnubCoreShared(String publish_key, String subscribe_key, String secret_key) {
        super(publish_key, subscribe_key, secret_key, "", false);
    }


    /**
    *
    * Constructor for Pubnub Class
    *
    * @param publish_key
    *            Publish Key
    * @param subscribe_key
    *            Subscribe Key
    * @param secret_key
    *            Secret Key
    * @param cipher_key
    *            Cipher Key
    * @param ssl_on
    *            SSL enabled ?
    * @param initialization_vector
    *            Initialization vector
    */

    public PubnubCoreShared(String publish_key, String subscribe_key,
                  String secret_key, String cipher_key, boolean ssl_on, String initialization_vector) {
        super(publish_key, subscribe_key, secret_key, cipher_key, ssl_on, initialization_vector);
    }

    @Override
    protected void init(String publish_key, String subscribe_key,
                        String secret_key, String cipher_key, boolean ssl_on, String initialization_vector) {
        super.init(publish_key, subscribe_key, secret_key, cipher_key, ssl_on, initialization_vector);

        originManagerExplicitlyEnabled = false;
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

    @Override
    public void shutdown() {
        super.shutdown();
        getOriginManager().stop();
    }

    /**
     * This method sets timeout value for subscribe/presence. Default value is
     * 310000 milliseconds i.e. 310 seconds
     *
     * @param timeout
     *            Timeout value in milliseconds for subscribe/presence
     */
    @Override
    public void setSubscribeTimeout(int timeout) {
        super.setSubscribeTimeout(timeout);
    }

    /**
     * This method returns timeout value for subscribe/presence.
     *
     * @return Timeout value in milliseconds for subscribe/presence
     */
    @Override
    public int getSubscribeTimeout() {
        return super.getSubscribeTimeout();
    }

    /**
     * This method set timeout value for non subscribe operations like publish,
     * history, hereNow. Default value is 15000 milliseconds i.e. 15 seconds.
     *
     * @param timeout
     *            Timeout value in milliseconds for Non subscribe operations
     *            like publish, history, hereNow
     */
    @Override
    public void setNonSubscribeTimeout(int timeout) {
        super.setNonSubscribeTimeout(timeout);
    }

    /**
     * This method returns timeout value for non subscribe operations like publish, history, hereNow
     *
     * @return Timeout value in milliseconds for for Non subscribe operations like publish, history, hereNow
     */
    @Override
    public int getNonSubscribeTimeout() {
        return super.getNonSubscribeTimeout();
    }

    public String getPrimaryOrigin() {
        return super.getOrigin();
    }

    /**
     * Returns current origin
     *
     * @return origin
     */
    @Override
    public String getOrigin() {
        if (isOriginManagerRunning()) {
            return (String) originsPool.toArray()[0];
        } else {
            return getPrimaryOrigin();
        }
    }

    public Set<String> getOriginsPool() {
        return originsPool;
    }

    public void setOriginsPool(LinkedHashSet<String> originsPool) throws PubnubException {
        setOriginsPool(originsPool, false);
    }

    public void setOriginsPool(LinkedHashSet<String> originsPool, boolean explicitlyEnableOriginManager)
            throws PubnubException {
        if (originsPool.size() < 2) {
            throw new PubnubException("It should be at least 2 origins in Origins Pool");
        }

        this.originsPool = Collections.synchronizedSet(originsPool);

        if (explicitlyEnableOriginManager) {
            this.originManagerExplicitlyEnabled = true;
            triggerOriginManager();
        }
    }

    /**
     * Start origin manager
     */
    protected void startOriginManager() {
        getOriginManager().start();
    }

    /**
     * Stops Current Origin and Failback Origin managers
     */
    protected void stopOriginManager() {
        getOriginManager().stop();
    }

    /**
     * Method to invoke on any OM-dependent operation. Starts/stops OM depending on current and new states.
     */
    public synchronized void triggerOriginManager() {
        boolean isCurrentlySubscribed = isCurrentlySubscribed();
        boolean isOriginManagerRunning = isOriginManagerRunning();

        if (originManagerExplicitlyEnabled && !isOriginManagerRunning && isCurrentlySubscribed) {
            startOriginManager();
        } else if ((!originManagerExplicitlyEnabled && isOriginManagerRunning)
                || (isOriginManagerRunning && !isCurrentlySubscribed)) {
            stopOriginManager();
        }
    }

    public boolean isOriginManagerRunning() {
        return getOriginManager().isCurrentOriginManagerRunning();
    }

    /**
     * Explicitly enables Origin Manager.
     *
     * @throws PubnubException if origins pool is not assigned yet
     */
    public void enableOriginManager() throws PubnubException {
        if (originsPool == null || originsPool.size() < 2) {
            throw new PubnubException("Origins Pool set should be assigned before #enableOriginManager() method invocation");
        }

        this.originManagerExplicitlyEnabled = true;
        triggerOriginManager();
    }

    public void disableOriginManager() {
        this.originManagerExplicitlyEnabled = false;
        triggerOriginManager();
    }

    protected OriginManager getOriginManager() {
        if (this.originManager == null) {
            this.originManager = new OriginManager(this);
        }

        return this.originManager;
    }

    public int getOriginHeartbeatInterval() {
        return originHeartbeatInterval;
    }

    public void setOriginHeartbeatInterval(int originHeartbeatInterval) {
        this.originHeartbeatInterval = originHeartbeatInterval;
    }

    public int getOriginHeartbeatIntervalAfterFailure() {
        return originHeartbeatIntervalAfterFailure;
    }

    public void setOriginHeartbeatIntervalAfterFailure(int originHeartbeatIntervalAfterFailure) {
        this.originHeartbeatIntervalAfterFailure = originHeartbeatIntervalAfterFailure;
    }

    public int getOriginHeartbeatMaxRetries() {
        return originHeartbeatMaxRetries;
    }

    public void setOriginHeartbeatMaxRetries(int originHeartbeatMaxRetries) {
        this.originHeartbeatMaxRetries = originHeartbeatMaxRetries;
    }

    public TimedTaskManager getTimedTaskManager() {
        return timedTaskManager;
    }

    @Override
    protected void resetSubscribeHttpManager() {
        super.resetSubscribeHttpManager();
        triggerOriginManager();
    }

    private String pamSign(String key, String data) throws PubnubException {
        Mac sha256_HMAC;

        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(),
                    "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hmacData = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            return new String(Base64Encoder.encode(hmacData)).replace('+', '-')
                   .replace('/', '_');
        } catch (InvalidKeyException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 1, "Invalid Key : " + e1.toString()));
        } catch (NoSuchAlgorithmException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 2, "Invalid Algorithm : " + e1.toString()));
        } catch (IllegalStateException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 3, "Invalid State : " + e1.toString()));
        } catch (UnsupportedEncodingException e1) {
            throw new PubnubException(getErrorObject(PubnubError.PNERROBJ_ULSSIGN_ERROR, 4, "Unsupported encoding : " + e1.toString()));
        }
    }

    /** Grant r/w access based on channel and auth key
     * @param channel
     * @param auth_key
     * @param read
     * @param write
     * @param callback
     */
    public void pamGrant(final String channel, String auth_key, boolean read,
                         boolean write, final Callback callback) {
        pamGrant(channel, auth_key, read, write, -1, callback);
    }

    /** Grant r/w access based on channel
     * @param channel
     * @param read
     * @param write
     * @param callback
     */
    public void pamGrant(final String channel, boolean read,
                         boolean write, final Callback callback) {
        pamGrant(channel, null, read, write, -1, callback);
    }

    /** Grant r/w access based on channel
     * @param channel
     * @param read
     * @param write
     * @param ttl
     * @param callback
     */
    public void pamGrant(final String channel, boolean read,
                         boolean write, int ttl, final Callback callback) {
        pamGrant(channel, null, read, write, ttl, callback);
    }

    /** Grant r/w access based on channel and auth key
     * @param channel
     * @param auth_key
     * @param read
     * @param write
     * @param ttl
     * @param callback
     */
    public void pamGrant(final String channel, String auth_key, boolean read,
                         boolean write, int ttl, Callback callback) {
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        String r = (read) ? "1" : "0";
        String w = (write) ? "1" : "0";

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel,
                                   getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 1));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "grant" + "\n" ;

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + auth_key + "&"  ;

        sign_input += "channel=" + PubnubUtil.urlEncode(channel) + "&" + "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&" + "r=" + r + "&" + "timestamp=" + timestamp
                            + ((ttl >= -1)?"&" + "ttl=" + ttl:"")
                            + "&" + "w=" + w;


        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel,
                                   e1.getPubnubError());
            return;
        }


        parameters.put("w", w);
        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("r", r);
        parameters.put("channel", channel);

        if (auth_key != null && auth_key.length() > 0 ) parameters.put("auth", auth_key);
        if (ttl >= -1) parameters.put("ttl", String.valueOf(ttl));

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "grant", "sub-key",
                                   this.SUBSCRIBE_KEY
                                 };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
        new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                invokeCallback(channel, response, "payload", cb, 4);
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                cb.errorCallback(channel, error);
                return;
            }
        });

        _request(hreq, nonSubscribeManager);

    }

    public void pamGrantChannelGroup(final String group, boolean read,
                                     boolean management, Callback callback) {
        pamGrantChannelGroup(group, read, management, -1, callback);
    }

    public void pamGrantChannelGroup(final String group, boolean read,
                                     boolean management, int ttl, Callback callback) {
        pamGrantChannelGroup(group, null, read, management, ttl, callback);
    }

    public void pamGrantChannelGroup(final String group, String auth_key, boolean read,
                                     boolean management, Callback callback) {
        pamGrantChannelGroup(group, auth_key, read, management, -1, callback);
    }

    public void pamGrantChannelGroup(final String group, String auth_key, boolean read, boolean management, int ttl,
                                               Callback callback) {
        String signature;
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        String r = (read) ? "1" : "0";
        String m = (management) ? "1" : "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(group, getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 1));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "grant" + "\n";

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + auth_key + "&"  ;

        sign_input += "channel-group=" + PubnubUtil.urlEncode(group) + "&"
                + "m=" + m + "&"
                + "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&"
                + "r=" + r + "&"
                + "timestamp=" + timestamp
                + ((ttl >= -1)?"&" + "ttl=" + ttl:"");

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(group, e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("r", r);
        parameters.put("m", m);
        parameters.put("channel-group", group);

        if (ttl >= -1) parameters.put("ttl", String.valueOf(ttl));
        if (auth_key != null && auth_key.length() > 0 ) parameters.put("auth", auth_key);

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "grant", "sub-key",
                this.SUBSCRIBE_KEY
        };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
                new ResponseHandler() {
                    public void handleResponse(HttpRequest hreq, String response) {
                        invokeCallback(group, response, "payload", cb, 4);
                    }

                    public void handleError(HttpRequest hreq, PubnubError error) {
                        cb.errorCallback(group, error);
                    }
                });

        _request(hreq, nonSubscribeManager);
    }

    /** ULS Audit
     * @param callback
     */
    public void pamAudit(Callback callback) {

        final Callback cb = getWrappedCallback(callback);

        Hashtable parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback("",
                                   getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 2));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n"
                            + "audit" + "\n" + "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&"
                            + "timestamp=" + timestamp;


        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback("",
                                   e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "audit", "sub-key",
                                   this.SUBSCRIBE_KEY
                                 };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
        new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                invokeCallback("", response, "payload", cb, 5 );
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                cb.errorCallback("", error);
                return;
            }
        });

        _request(hreq, nonSubscribeManager);

    }

    /** ULS audit by channel
     * @param channel
     * @param callback
     */
    public void pamAudit(final String channel,
                         Callback callback) {

        final Callback cb = getWrappedCallback(callback);

        Hashtable parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel,
                                   getErrorObject(PNERROBJ_SECRET_KEY_MISSING , 3));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n"
                            + "audit" + "\n" + "channel="
                            + PubnubUtil.urlEncode(channel) + "&" + "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&" + "timestamp=" + timestamp;

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel,
                                   e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("channel", channel);

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "audit", "sub-key",
                                   this.SUBSCRIBE_KEY
                                 };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
        new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                invokeCallback(channel, response, "payload", cb, 6);
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                cb.errorCallback(channel, error);
                return;
            }
        });

        _request(hreq, nonSubscribeManager);

    }

    /** ULS audit by channel and auth key
     * @param channel
     * @param auth_key
     * @param callback
     */
    public void pamAudit(final String channel, String auth_key,
                         Callback callback) {

        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel,
                                   getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 4));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n"
                            + "audit" + "\n" + "auth=" + PubnubUtil.urlEncode(auth_key) + "&" + "channel="
                            + PubnubUtil.urlEncode(channel) + "&" + "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&" + "timestamp=" + timestamp;


        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel,
                                   e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("channel", channel);
        parameters.put("auth", auth_key);

        String[] urlComponents = { getPubnubUrl(), "v1", "auth", "audit", "sub-key",
                                   this.SUBSCRIBE_KEY
                                 };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
        new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response) {
                invokeCallback(channel, response, "payload", cb, 2);
            }

            public void handleError(HttpRequest hreq, PubnubError error) {
                cb.errorCallback(channel, error);
                return;
            }
        });

        _request(hreq, nonSubscribeManager);

    }

    public void pamAuditChannelGroup(final String group, Callback callback) {
        pamAuditChannelGroup(group, null, callback);
    }

    public void pamAuditChannelGroup(final String group, String auth_key, Callback callback) {
        String signature;
        final Callback cb = getWrappedCallback(callback);

        Hashtable parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(group, getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 3));
            return;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n";

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + auth_key + "&"  ;

        sign_input += "channel-group=" + PubnubUtil.urlEncode(group) + "&"
                + "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&"
                + "timestamp=" + timestamp;

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(group, e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("channel-group", group);

        if (auth_key != null && auth_key.length() > 0 ) parameters.put("auth", auth_key);

        String[] urlComponents = {getPubnubUrl(), "v1", "auth", "audit", "sub-key",
                this.SUBSCRIBE_KEY
        };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
                new ResponseHandler() {
                    public void handleResponse(HttpRequest hreq, String response) {
                        invokeCallback(group, response, "payload", cb, 6);
                    }

                    public void handleError(HttpRequest hreq, PubnubError error) {
                        cb.errorCallback(group, error);
                    }
                });

        _request(hreq, nonSubscribeManager);
    }

    /** ULS revoke by channel and auth key
     * @param channel
     * @param auth_key
     * @param callback
     */
    public void pamRevoke(String channel, String auth_key, Callback callback) {
        pamGrant(channel, auth_key, false, false, callback);
    }

    /** ULS revoke by channel
     * @param channel
     * @param callback
     */
    public void pamRevoke(String channel, Callback callback) {
        pamGrant(channel, null, false, false, callback);
    }

    public void pamRevokeChannelGroup(String group, Callback callback) {
        pamRevokeChannelGroup(group, null, callback);
    }

    public void pamRevokeChannelGroup(String group, String auth_key, Callback callback) {
        pamGrantChannelGroup(group, auth_key, false, false, -1, callback);
    }
}
