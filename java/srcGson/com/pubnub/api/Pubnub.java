package com.pubnub.api;

import java.util.Hashtable;

import com.google.gson.*;


public class Pubnub extends PubnubPlatform {

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
    
    /**
     * Send a message to a channel.
     *
     * @param channel
     *            Channel name
     * @param message
     *            PnJsonObject to be published
     * @param callback
     *            object of sub class of Callback class
     */
    public void publish(String channel, JsonObject message, boolean storeInHistory, Callback callback) {
    	publish(channel, new PnJsonObject(message), storeInHistory, callback);
    }

    /**
     * Send a message to a channel.
     *
     * @param channel
     *            Channel name
     * @param message
     *            JSONOArray to be published
     * @param callback
     *            object of sub class of Callback class
     */
    public void publish(String channel, JsonArray message, boolean storeInHistory, Callback callback) {
    	publish(channel, new PnJsonArray(message), storeInHistory, callback);
    }
    

    /**
     * Send a message to a channel.
     *
     * @param channel
     *            Channel name
     * @param message
     *            PnJsonObject to be published
     * @param callback
     *            object of sub class of Callback class
     */
    public void publish(String channel, JsonObject message, Callback callback) {
    	publish(channel, new PnJsonObject(message), callback);
    }

    /**
     * Send a message to a channel.
     *
     * @param channel
     *            Channel name
     * @param message
     *            JSONOArray to be published
     * @param callback
     *            object of sub class of Callback class
     */
    public void publish(String channel, JsonArray message, Callback callback) {
    	publish(channel, new PnJsonArray(message), callback);
    }
    

    public void setState(String channel, String uuid, JsonObject state, Callback callback) {
    	setState(channel, uuid, new PnJsonObject(state), callback);
    }
    
    public void channelGroupSetState(String group, String uuid, JsonObject state, Callback callback) {
    	channelGroupSetState(group, uuid, new PnJsonObject(state), callback);
    }
    

}
