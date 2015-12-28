package com.pubnub.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.pubnub.api.PubnubError.*;
import static com.pubnub.api.PubnubUtil.*;

/**
 * Pubnub object facilitates querying channels for messages and listening on
 * channels for presence/message events
 *
 * @author Pubnub
 *
 */

public class Pubnub extends PubnubCoreShared  {

    public Pubnub() {

    }

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
    public Pubnub(String publish_key, String subscribe_key, String secret_key,
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
    public Pubnub(String publish_key, String subscribe_key, String secret_key,
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
    public Pubnub(String publish_key, String subscribe_key) {
        super(publish_key, subscribe_key, "", "", false);
    }

    /**
     * @param publish_key
     *            Publish Key
     * @param subscribe_key
     *            Subscribe Key
     * @param ssl
     */
    public Pubnub(String publish_key, String subscribe_key, boolean ssl) {
        super(publish_key, subscribe_key, "", "", ssl);
    }

    /**
     * @param publish_key
     * @param subscribe_key
     * @param secret_key
     */
    public Pubnub(String publish_key, String subscribe_key, String secret_key) {
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

    public Pubnub(String publish_key, String subscribe_key,
                  String secret_key, String cipher_key, boolean ssl_on, String initialization_vector) {
        super(publish_key, subscribe_key, secret_key, cipher_key, ssl_on, initialization_vector);
    }

    protected String getUserAgent() {
        return "Java/" + VERSION;
    }

    public static class Builder
    {


        private String domain;
        private String origin;
        private String publishKey = "";
        private String subscribeKey = "";
        private String secretKey = "";
        private String cipherKey = "";
        private String authKey;
        private String uuid;
        private boolean cacheBusting;
        private boolean ssl;


        public Builder setDomain(String domain) { this.domain = domain; return this; }
        public Builder setOrigin(String origin) { this.origin = origin; return this; }
        public Builder setPublishKey(String publishKey) { this.publishKey = publishKey; return this; }
        public Builder setSubscribeKey(String subscribeKey) { this.subscribeKey = subscribeKey; return this; }
        public Builder setSecretKey(String secretKey) { this.secretKey = secretKey; return this; }
        public Builder setCipherKey(String cipherKey) { this.cipherKey = cipherKey; return this; }
        public Builder setAuthKey(String authKey) { this.authKey = authKey; return this; }
        public Builder setUuid(String uuid) { this.uuid = uuid; return this; }
        public Builder setSsl(boolean ssl) { this.ssl = ssl; return this; }


        public Pubnub build()
        {
            Pubnub pubnub = new Pubnub();
            pubnub.setPublishKey(publishKey);
            pubnub.setSubscribeKey(subscribeKey);
            pubnub.setSecretKey(secretKey);
            pubnub.setCipherKey(cipherKey);
            pubnub.setSSL(ssl);
            pubnub.init();
            pubnub.initAsync();
            return pubnub;
        }
    }
}
