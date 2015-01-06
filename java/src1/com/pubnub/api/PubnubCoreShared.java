package com.pubnub.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

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

    /**
     * This method sets timeout value for subscribe/presence. Default value is
     * 310000 milliseconds i.e. 310 seconds
     *
     * @param timeout
     *            Timeout value in milliseconds for subscribe/presence
     */
    public void setSubscribeTimeout(int timeout) {
        super.setSubscribeTimeout(timeout);
    }

    /**
     * This method returns timeout value for subscribe/presence.
     *
     * @return Timeout value in milliseconds for subscribe/presence
     */
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
    public void setNonSubscribeTimeout(int timeout) {
        super.setNonSubscribeTimeout(timeout);
    }
    /**
     * This method returns timeout value for non subscribe operations like publish, history, hereNow
     *
     * @return Timeout value in milliseconds for for Non subscribe operations like publish, history, hereNow
     */
    public int getNonSubscribeTimeout() {
        return super.getNonSubscribeTimeout();
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
    public void pamGrant(final String channel, String auth_key, Boolean read,
                         Boolean write, final Callback callback) {
        pamGrant(channel, auth_key, read, write, null, callback);
    }

    /** Grant r/w access based on channel
     * @param channel
     * @param read
     * @param write
     * @param callback
     */
    public void pamGrant(final String channel, Boolean read,
                         Boolean write, final Callback callback) {
        pamGrant(channel, null, read, write, null, callback);
    }

    /** Grant r/w access based on channel
     * @param channel
     * @param read
     * @param write
     * @param ttl
     * @param callback
     */
    public void pamGrant(final String channel, Boolean read,
                         Boolean write, Integer ttl, final Callback callback) {
        pamGrant(channel, null, read, write, ttl, callback);
    }

    public void pamGrant(String channel, String auth_key, Boolean read,
                         Boolean write, Integer ttl, Callback callback) {
        _pamGrant(channel, null, null, read, write, null, auth_key, ttl, callback);
    }

    public void pamGrantChannelGroup(final String group, Boolean read,
                                     Boolean management, Callback callback) {
        pamGrantChannelGroup(group, read, management, null, callback);
    }

    public void pamGrantChannelGroup(final String group, Boolean read,
                                     Boolean management, Integer ttl, Callback callback) {
        pamGrantChannelGroup(group, null, read, management, ttl, callback);
    }

    public void pamGrantChannelGroup(final String group, String auth_key, Boolean read,
                                     Boolean management, Callback callback) {
        pamGrantChannelGroup(group, auth_key, read, management, null, callback);
    }

    public void pamGrantChannelGroup(final String group, String auth_key, Boolean read, Boolean management, Integer ttl,
                                               Callback callback) {
        _pamGrant(null, group, null, read, null, management, auth_key, ttl, callback);
    }

    public void pamGrantSyncedObject(final String syncedObject, Boolean read,
                                     Boolean write, Callback callback) {
        pamGrantSyncedObject(syncedObject, read, write, null, callback);
    }

    public void pamGrantSyncedObject(final String syncedObject, Boolean read,
                                     Boolean write, Integer ttl, Callback callback) {
        pamGrantSyncedObject(syncedObject, null, read, write, ttl, callback);
    }

    public void pamGrantSyncedObject(final String syncedObject, String auth_key, Boolean read,
                                     Boolean write, Callback callback) {
        pamGrantSyncedObject(syncedObject, auth_key, read, write, null, callback);
    }

    public void pamGrantSyncedObject(final String syncedObject, String auth_key, Boolean read, Boolean write, Integer ttl,
                                     Callback callback) {
        _pamGrant(null, null, syncedObject, read, write, null, auth_key, ttl, callback);
    }

    /**
     * Grant r/w access based on channel and auth key
     *
     * @param channel name to grant permissions to
     * @param group name to grant permissions to
     * @param syncedObject name to grant permissions to
     * @param read permission flag
     * @param write permission flag
     *                   will be assigned only while granting access to channel or synced object
     * @param management permission flag
     *                   will be assigned only while granting access to channel group
     * @param auth_key string to grant permissions to
     * @param ttl time to live.
     *            0 - indefinitely
     *            1 - min
     *            1440 - default
     *            525600 - max
     * @param callback to invoke
     */
    protected void _pamGrant(final String channel, final String group, final String syncedObject,
                           Boolean read, Boolean write, Boolean management,
                           String auth_key, Integer ttl, Callback callback) {
        final Callback cb = getWrappedCallback(callback);
        Hashtable<String, String> parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");
        String signature;
        String sign_input;

        String r = (read != null && read) ? "1" : "0";
        String m = (management != null && management) ? "1" : "0";
        String w = (write != null && write) ? "1" : "0";

        Boolean isTTLSet = (ttl != null && ttl >= 0);

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(group, getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 1));
            return;
        }

        // WARNING: sign_input string elements should be sorted by alphabet
        sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "grant" + "\n";

        if (auth_key != null && auth_key.length() > 0) {
            sign_input += "auth=" + auth_key + "&";
            parameters.put("auth", auth_key);
        }

        if (PubnubUtil.isPresent(channel)) {
            sign_input += "channel=" + PubnubUtil.urlEncode(channel) + "&";
        } else if (PubnubUtil.isPresent(group)) {
            sign_input += "channel-group=" + PubnubUtil.urlEncode(group) + "&" +
                    "m=" + m + "&";
            parameters.put("channel-group", group);
            parameters.put("m", m);
        } else if (PubnubUtil.isPresent(syncedObject)) {
            sign_input += "obj-id=" + PubnubUtil.urlEncode(syncedObject) + "&";
            parameters.put("obj-id", syncedObject);
        }

        sign_input += "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&"
                + "r=" + r + "&"
                + "timestamp=" + timestamp
                + (isTTLSet ? "&" + "ttl=" + ttl : "");

        if (channel != null || syncedObject != null) {
            sign_input += "&" + "w=" + w;
            parameters.put("w", w);
        }

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(group, e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("r", r);

        if (isTTLSet) parameters.put("ttl", String.valueOf(ttl));

        String[] urlComponents = {getPubnubUrl(), "v1", "auth", "grant", "sub-key",
                this.SUBSCRIBE_KEY
        };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
                new ResponseHandler() {
                    public void handleResponse(HttpRequest hreq, String response) {
                        if (channel != null) {
                            invokeCallback(channel, response, "payload", cb, 4);
                        } else {
                            invokeCallback(null, response, "payload", cb, 4);
                        }
                    }

                    public void handleError(HttpRequest hreq, PubnubError error) {
                        if (channel != null) {
                            cb.errorCallback(group, error);
                        } else {
                            cb.errorCallback(null, error);
                        }
                    }
                });

        _request(hreq, nonSubscribeManager);
    }


    /** ULS Audit
     * @param callback to invoke
     */
    public void pamAudit(Callback callback) {
        pamAudit(null, callback);
    }

    /** ULS audit by channel
     * @param channel name
     * @param callback to invoke
     */
    public void pamAudit(final String channel, Callback callback) {
        pamAudit(channel, null, callback);
    }

    /** ULS audit by channel and auth key
     * @param channel name
     * @param auth_key of user
     * @param callback to invoke
     */
    public void pamAudit(final String channel, String auth_key, Callback callback) {
        _pamAudit(channel, null, null, auth_key, callback);
    }

    public void pamAuditChannelGroup(final String group, Callback callback) {
        pamAuditChannelGroup(group, null, callback);
    }

    public void pamAuditChannelGroup(final String group, String auth_key, Callback callback) {
        _pamAudit(null, group, null, auth_key, callback);
    }

    public void pamAuditSyncedObject(final String syncedObject, Callback callback) {
        pamAuditSyncedObject(syncedObject, null, callback);
    }

    public void pamAuditSyncedObject(final String syncedObject, String auth_key, Callback callback) {
        _pamAudit(null, null, syncedObject, auth_key, callback);
    }

    private void _pamAudit(final String channel, final String group, String syncedObject, String auth_key, Callback callback) {
        String signature;
        final Callback cb = getWrappedCallback(callback);

        Hashtable<String, String> parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(group, getErrorObject(PNERROBJ_SECRET_KEY_MISSING, 3));
            return;
        }

        // WARNING: sign_input string elements should be sorted by alphabet
        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n";

        if (PubnubUtil.isPresent(auth_key) && auth_key.length() > 0) {
            sign_input += "auth=" + auth_key + "&";
            parameters.put("auth", auth_key);
        }

        if (PubnubUtil.isPresent(channel)) {
            sign_input += "channel=" + PubnubUtil.urlEncode(channel) + "&";
        } else if (PubnubUtil.isPresent(group)) {
            sign_input += "channel-group=" + PubnubUtil.urlEncode(group) + "&";
            parameters.put("channel-group", group);
        } else if (PubnubUtil.isPresent(syncedObject)) {
            sign_input += "obj-id=" + PubnubUtil.urlEncode(syncedObject) + "&";
            parameters.put("obj-id", syncedObject);
        }

        sign_input += "pnsdk=" + PubnubUtil.urlEncode(getUserAgent()) + "&"
                + "timestamp=" + timestamp;

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(group, e1.getPubnubError());
            return;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);

        String[] urlComponents = {getPubnubUrl(), "v1", "auth", "audit", "sub-key",
                this.SUBSCRIBE_KEY
        };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters,
                new ResponseHandler() {
                    public void handleResponse(HttpRequest hreq, String response) {
                        if (channel != null) {
                            invokeCallback(channel, response, "payload", cb, 6);
                        } else {
                            invokeCallback(null, response, "payload", cb, 6);
                        }
                    }

                    public void handleError(HttpRequest hreq, PubnubError error) {
                        if (channel != null) {
                            cb.errorCallback(group, error);
                        } else {
                            cb.errorCallback(null, error);
                        }
                    }
                });

        _request(hreq, nonSubscribeManager);
    }

    /** ULS revoke by channel and auth key
     * @param channel name
     * @param auth_key of user
     * @param callback to invoke
     */
    public void pamRevoke(String channel, String auth_key, Callback callback) {
        pamGrant(channel, auth_key, false, false, callback);
    }

    /** ULS revoke by channel
     * @param channel to revoke
     * @param callback to invoke
     */
    public void pamRevoke(String channel, Callback callback) {
        pamGrant(channel, null, false, false, callback);
    }

    public void pamRevokeChannelGroup(String group, Callback callback) {
        pamRevokeChannelGroup(group, null, callback);
    }

    public void pamRevokeChannelGroup(String group, String auth_key, Callback callback) {
        pamGrantChannelGroup(group, auth_key, false, false, callback);
    }

    public void pamRevokeSyncedObject(String syncedObject, Callback callback) {
        pamRevokeSyncedObject(syncedObject, null, callback);
    }

    public void pamRevokeSyncedObject(String syncedObject, String auth_key, Callback callback) {
        pamGrantSyncedObject(syncedObject, auth_key, false, false, callback);
    }
}
