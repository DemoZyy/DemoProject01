package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

abstract public class PubnubCore implements PubnubInterface {

    protected static String VERSION = "";
    protected volatile boolean CACHE_BUSTING = true;

    protected String HOSTNAME = "pubsub";
    protected int HOSTNAME_SUFFIX = 1;
    protected String DOMAIN = "pubnub.com";
    protected String ORIGIN_STR = null;
    protected String ORIGIN = "pubsub.pubnub.com";
    protected String PUBLISH_KEY = "";
    protected String SUBSCRIBE_KEY = "";
    protected String SECRET_KEY = "";
    protected String CIPHER_KEY = "";
    protected String IV = null;
    protected volatile String AUTH_STR = null;
    private Random generator = new Random();

    protected Hashtable params = new Hashtable();

    private boolean SSL = true;
    protected String UUID = null;

    protected SubscribeManager subscribeManager;
    protected NonSubscribeManager nonSubscribeManager;

    protected abstract String getUserAgent();

    protected HttpResponse fetch(String url) throws IOException, PubnubException {
        return null;
    }
    
    public PubnubCore() {
        
    }
    
    public static class Builder
    {


        private String domain = "pubnub.com";
        private String origin = "pubsub.pubnub.com";
        private String publishKey = "";
        private String subscribeKey = "";
        private String secretKey = "";
        private String cipherKey = "";
        private String authKey;
        private String uuid;
        private boolean cacheBusting;
        private boolean ssl;
        private String IV;


        public Builder setDomain(String domain) { this.domain = domain; return this; }
        public Builder setOrigin(String origin) { this.origin = origin; return this; }
        public Builder setPublishKey(String publishKey) { this.publishKey = publishKey; return this; }
        public Builder setSubscribeKey(String subscribeKey) { this.subscribeKey = subscribeKey; return this; }
        public Builder setSecretKey(String secretKey) { this.secretKey = secretKey; return this; }
        public Builder setCipherKey(String cipherKey) { this.cipherKey = cipherKey; return this; }
        public Builder setAuthKey(String authKey) { this.authKey = authKey; return this; }
        public Builder setUuid(String uuid) { this.uuid = uuid; return this; }
        public Builder setSsl(boolean ssl) { this.ssl = ssl; return this; }
        public Builder setInitializationVector(String initializationVector) { this.IV = initializationVector; return this; }
        public Builder setCacheBusting(boolean cacheBusting) { this.cacheBusting = cacheBusting; return this; }
 

        public Pubnub build()
        {
            Pubnub pubnub = new Pubnub();
            pubnub.init();
            pubnub.initAsync();
            pubnub.setPublishKey(publishKey);
            pubnub.setSubscribeKey(subscribeKey);
            pubnub.setSecretKey(secretKey);
            pubnub.setCipherKey(cipherKey);
            pubnub.setSSL(ssl);
            pubnub.setInitializationVector(IV);
            pubnub.setCacheBusting(cacheBusting);
            pubnub.setOrigin(origin);
            pubnub.setAuthKey(authKey);
            return pubnub;
        }
        public PubnubSync buildSync(){
            PubnubSync pubnub = new PubnubSync();
            pubnub.init();
            pubnub.setPublishKey(publishKey);
            pubnub.setSubscribeKey(subscribeKey);
            pubnub.setSecretKey(secretKey);
            pubnub.setCipherKey(cipherKey);
            pubnub.setSSL(ssl);
            pubnub.setCacheBusting(cacheBusting);
            pubnub.setInitializationVector(IV);
            pubnub.setOrigin(origin);
            pubnub.setAuthKey(authKey);
            pubnub.init();
            return pubnub;
        }
    }
    public void setInitializationVector(String initializationVector) {
        this.IV = initializationVector;
    }
    
    public void setPublishKey(String publishKey) {
        this.PUBLISH_KEY = publishKey;
    }
    
    public void setSubscribeKey(String subscribeKey) {
        this.SUBSCRIBE_KEY = subscribeKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.SECRET_KEY = secretKey;
    }
    
    public void setCipherKey(String cipherKey) {
        this.CIPHER_KEY = cipherKey;
    }
    
    public void setSSL(boolean ssl) {
        this.SSL = ssl;
    }

    public void setOrigin(String origin) {
        this.ORIGIN = origin;
        this.CACHE_BUSTING = false;
    }
    
    public void setCacheBusting(boolean cacheBusting) {
        this.CACHE_BUSTING = cacheBusting;
    }
    
    void init() {

        if (UUID == null)
            UUID = uuid();

        if (params == null)
            params = new Hashtable();

        params.put("pnsdk", getUserAgent());

    }

    // abstract String uuid();

    protected String getPubnubUrl(Result result) {

        if (ORIGIN_STR == null) {
            // SSL On?
            if (this.SSL) {
                ORIGIN_STR = "https://";
            } else {
                ORIGIN_STR = "http://";
            }
            if (this.CACHE_BUSTING) {
                ORIGIN_STR += HOSTNAME;
                ORIGIN_STR += "-" + String.valueOf(HOSTNAME_SUFFIX);
                ORIGIN_STR += "." + DOMAIN;               
            } else {
                ORIGIN_STR += ORIGIN;
            }


        }
        if (result != null) {
            result.config.origin = ORIGIN_STR.split("://")[1];
            result.config.TLS = this.SSL;
            result.config.uuid = this.UUID;
            result.config.authKey = this.AUTH_STR;
        }
        return ORIGIN_STR;
    }


    public String getOrigin() {
        /*
        if (ORIGIN_STR != null) {
            return ORIGIN_STR.split("://")[1];
        } else {
            return this.HOSTNAME + "." + this.DOMAIN;
        }
        */
        return this.ORIGIN;
    }

    public String getAuthKey() {
        return this.AUTH_STR;
    }

    public void setAuthKey(String authKey) {

        this.AUTH_STR = authKey;
        if (authKey == null || authKey.length() == 0) {
            params.remove("auth");
        } else {
            params.put("auth", this.AUTH_STR);
        }
    }

    public void unsetAuthKey() {
        this.AUTH_STR = null;
        params.remove("auth");
    }

    protected int getRandom() {
        return Math.abs(this.generator.nextInt());
    }

    protected Callback voidCallback = new Callback() {
        @Override
        void successCallback(String channel, Object message, Result result) {
            
        }
        
        @Override
        void errorCallback(String channel, PubnubError error, Result result) {
            
        }
    };

    protected Callback getWrappedCallback(Callback callback) {
        if (callback == null) {
            return voidCallback;
        } else
            return callback;
    }

    protected PubnubError getPubnubError(PubnubException px, PubnubError error, int code, String message) {
        PubnubError pe = px.getPubnubError();
        if (pe == null) {
            pe = PubnubError.getErrorObject(error, code, message);
        }
        return pe;
    }

    protected void decryptJSONArray(JSONArray messages) throws JSONException, IllegalStateException, IOException,
            PubnubException {
        if (CIPHER_KEY.length() > 0) {
            for (int i = 0; i < messages.length(); i++) {
                PubnubCrypto pc = new PubnubCrypto(CIPHER_KEY, IV);

                String message;
                message = pc.decrypt(messages.get(i).toString());
                messages.put(i, PubnubUtil.stringToJSON(message));
            }
        }
    }


    public void setUUID(String uuid) {
        this.UUID = uuid;
    }

    public String getUUID() {
        return this.UUID;
    }

    protected Object _publish(Hashtable args, boolean sync) {

        final String channel = (String) args.get("channel");
        final Object message = args.get("message");
        Callback cb = (Callback) args.get("callback");

        String storeInHistory = (String) args.get("storeInHistory");
        String msgStr = message.toString();
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        PublishStatus result = new PublishStatus();

        if (storeInHistory != null && storeInHistory.length() > 0)
            parameters.put("store", storeInHistory);


        final Callback callback = getWrappedCallback(cb);

        if (this.CIPHER_KEY.length() > 0) {
            // Encrypt Message
            PubnubCrypto pc = new PubnubCrypto(this.CIPHER_KEY, this.IV);
            try {
                if (message instanceof String) {
                    msgStr = "\"" + msgStr + "\"";
                }
                msgStr = "\"" + pc.encrypt(msgStr) + "\"";
            } catch (PubnubException e) {
                callback.errorCallback(channel,
                        getPubnubError(e, PubnubError.PNERROBJ_ENCRYPTION_ERROR, 4, msgStr + " : " + e.toString())
                        , result);
                return null;
            }
        } else {
            if (message instanceof String) {
                msgStr = PubnubUtil.stringReplaceAll(msgStr, "\"", "\\\\\"");
                msgStr = "\"" + msgStr + "\"";
            }
        }

        // Generate String to Sign
        String signature = "0";

        if (this.SECRET_KEY.length() > 0) {
            StringBuffer string_to_sign = new StringBuffer();
            string_to_sign.append(this.PUBLISH_KEY).append('/').append(this.SUBSCRIBE_KEY).append('/')
                    .append(this.SECRET_KEY).append('/').append(channel).append('/').append(msgStr);

            // Sign Message
            try {
                signature = new String(PubnubCrypto.hexEncode(PubnubCrypto.md5(string_to_sign.toString())), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                PubnubError pe = PubnubError.getErrorObject(PubnubError.PNERROBJ_ENCRYPTION_ERROR, 6, msgStr + " : "
                        + e.toString());
                callback.errorCallback(channel, pe, result);
            } catch (PubnubException e) {
                callback.errorCallback(channel,
                        getPubnubError(e, PubnubError.PNERROBJ_ENCRYPTION_ERROR, 5, msgStr + " : " + e.toString()),
                        result);
            }
        }
        String[] urlComponents = { getPubnubUrl(result), "publish", this.PUBLISH_KEY, this.SUBSCRIBE_KEY,
                PubnubUtil.urlEncode(signature), PubnubUtil.urlEncode(channel), "0", PubnubUtil.urlEncode(msgStr) };

        class PublishResponseHandler extends ResponseHandler {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                JSONArray jsarr;
                try {
                    jsarr = new JSONArray(response);
                } catch (JSONException e) {
                    handleError(hreq, 
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_JSON, 1, response)
                            , result);
                    return;
                }
                callback.successCallback(channel, jsarr, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                callback.errorCallback(channel, error, result);
                return;
            }
        }
        HttpRequest hreq = new HttpRequest(urlComponents, parameters, new PublishResponseHandler(), result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);

    }

    JSONObject _whereNow(final String uuid, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        WhereNowResult result = new WhereNowResult();
        String[] urlargs = { getPubnubUrl(result), "v2", "presence", "sub_key", this.SUBSCRIBE_KEY, "uuid",
                PubnubUtil.urlEncode(uuid) };

        HttpRequest hreq = new HttpRequest(urlargs, params, new ResponseHandler() {
            
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback("", response, "payload", cb, 4, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
                return;
            }
        }, result);
        setResultData(result, OperationType.WHERE_NOW, hreq);
        return (JSONObject) _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _request(final HttpRequest hreq, RequestManager connManager, boolean abortExisting) {
        if (abortExisting) {
            connManager.resetHttpManager();
        }
        if (connManager == null) {
            try {
                HttpResponse resp = fetch(hreq.getUrl());
                return PubnubUtil.stringToJSON(resp.getResponse());

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            } catch (PubnubException e) {
                // System.out.println(e);
                return e.getErrorJsonObject();
            }
        }
        connManager.queue(hreq);
        return null;
    }

    protected Object _request(final HttpRequest hreq, RequestManager simpleConnManager) {
        return _request(hreq, simpleConnManager, false);
    }

    void sendNonSubscribeRequest(HttpRequest hreq) {
        _request(hreq, this.nonSubscribeManager);
    }

    protected JSONArray _time(Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);

        Result result = new Result();
        
        String[] url = { getPubnubUrl(result), "time", "0" };
        HttpRequest hreq = new HttpRequest(url, params, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                cb.successCallback(null, response, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }

        }, result);

        return (JSONArray) _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected void keepOnlyPluralSubscriptionItems(Hashtable args) {
        String _channel = (String) args.get("channel");
        String _group = (String) args.get("group");

        if (_channel != null && !(_channel.equals(""))) {
            args.put("channels", new String[] { _channel });
            args.remove("channel");
        }

        if (_group != null && !(_group.equals(""))) {
            args.put("groups", new String[] { _group });
            args.remove("group");
        }
    }

    protected boolean inputsValid(Hashtable args) throws PubnubException {
        boolean channelsOk;
        boolean groupsOk;

        /*
        if (!(args.get("callback") instanceof Callback) || args.get("callback") == null) {
            throw new PubnubException("Invalid Callback");
        }
        */

        String[] _channels = (String[]) args.get("channels");
        String[] _groups = (String[]) args.get("groups");

        channelsOk = (_channels != null && _channels.length > 0);
        groupsOk = (_groups != null && _groups.length > 0);

        if (!channelsOk && !groupsOk) {
            throw new PubnubException("Channel or Channel Group Missing");
        }

        return true;
    }
    

    protected Object _history(final String channel, long start, long end, int count, boolean reverse,
            boolean includeTimetoken, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        HistoryResult result = new HistoryResult();
                
        if (count == -1)
            count = 100;

        parameters.put("count", String.valueOf(count));
        parameters.put("reverse", String.valueOf(reverse));
        parameters.put("include_token", String.valueOf(includeTimetoken));

        if (start != -1)
            parameters.put("start", Long.toString(start).toLowerCase());

        if (end != -1)
            parameters.put("end", Long.toString(end).toLowerCase());

        String[] urlargs = { getPubnubUrl(result), "v2", "history", "sub-key", this.SUBSCRIBE_KEY, "channel",
                PubnubUtil.urlEncode(channel) };

        class HistoryResponseHandler extends ResponseHandler {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                setResultData(result, OperationType.HISTORY, hreq);
                JSONArray respArr;
                try {
                    respArr = new JSONArray(response);
                    decryptJSONArray((JSONArray) respArr.get(0));
                    cb.successCallback(channel, respArr, result);
                } catch (JSONException e) {
                    cb.errorCallback(channel, PubnubError.getErrorObject(PubnubError.PNERROBJ_JSON_ERROR, 3),
                            result);
                } catch (IOException e) {
                    cb.errorCallback(channel,
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_DECRYPTION_ERROR, 9, response),
                            result);
                } catch (PubnubException e) {
                    cb.errorCallback(
                            channel,
                            getPubnubError(e, PubnubError.PNERROBJ_DECRYPTION_ERROR, 10,
                                    response + " : " + e.toString()), result);
                } catch (Exception e) {
                    cb.errorCallback(
                            channel,
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_DECRYPTION_ERROR, 11,
                                    response + " : " + e.toString()), result);
                }

            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                setResultData(result, OperationType.HISTORY, hreq);
                cb.errorCallback(channel, error, result);
                return;
            }
        }

        HttpRequest hreq = new HttpRequest(urlargs, parameters, new HistoryResponseHandler(), result);
        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _hereNow(String[] channels, String[] channelGroups, boolean state, boolean uuids,
            Callback callback, boolean sync) {

        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        ArrayList urlArgs = new ArrayList();
        HereNowResult result = new HereNowResult();
        urlArgs.add(getPubnubUrl(result));
        urlArgs.add("v2");
        urlArgs.add("presence");
        urlArgs.add("sub_key");
        urlArgs.add(this.SUBSCRIBE_KEY);


        if (channels != null || channelGroups != null) {
            String channelsString = PubnubUtil.joinString(channels, ",");
            if ("".equals(channelsString)) {
                channelsString = ",";
            } else {
                channelsString = PubnubUtil.urlEncode(channelsString);
            }

            urlArgs.add("channel");
            urlArgs.add(channelsString);
        }

        if (state)
            parameters.put("state", "1");
        if (!uuids)
            parameters.put("disable_uuids", "1");
        if (channelGroups != null && channelGroups.length > 0) {
            parameters.put("channel-group", PubnubUtil.joinString(channelGroups, ","));
        }

        String[] path = (String[]) urlArgs.toArray(new String[urlArgs.size()]);

        HttpRequest hreq = new HttpRequest(path, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback(null, response, "payload", cb, 1, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }
        }, result);

        setResultData(result, OperationType.HERE_NOW_FOR_CHANNEL, hreq);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }


    protected void setResultData(Result result, OperationType operationType, HttpRequest hreq) {
        result.hreq = hreq;
        result.pubnub = this;
        result.config.origin = this.ORIGIN_STR;
        result.config.authKey = this.AUTH_STR;
        result.config.uuid = this.UUID;
        result.operation = operationType;
    }

    protected boolean validateInput(String name, Object input, Callback callback, Result result) {

        if (input == null) {
            callback.errorCallback("",
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_ARGUMENTS, 1, name + " cannot be null"), result);
            return false;
        }

        if (input instanceof String && ((String) input).length() == 0) {
            callback.errorCallback(
                    "",
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_ARGUMENTS, 2, name
                            + " cannot be zero length"), result);
            return false;
        }
        return true;
    }

    protected Object _setState(Subscriptions sub, String channel, String group, String uuid, JSONObject state,
            Callback callback, boolean sync) {
        SubscriptionItem item = sub.getItem(channel);
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        
        Result result = new Result();

        String[] urlArgs = { getPubnubUrl(result), "v2", "presence", "sub-key", this.SUBSCRIBE_KEY, "channel", channel,
                "uuid", PubnubUtil.urlEncode(uuid), "data" };

        if (state != null)
            parameters.put("state", state.toString());
        if (group != null)
            parameters.put("channel-group", group);

        if (item != null) {
            try {
                sub.state.put(channel, state);
            } catch (JSONException e) {

            }
        }

        HttpRequest hreq = new HttpRequest(urlArgs, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback("", response, "payload", cb, 2, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _getState(String channel, String uuid, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        Result result = new Result();

        String[] urlArgs = { getPubnubUrl(result), "v2", "presence", "sub-key", this.SUBSCRIBE_KEY, "channel",
                PubnubUtil.urlEncode(channel), "uuid", PubnubUtil.urlEncode(uuid) };

        HttpRequest hreq = new HttpRequest(urlArgs, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback("", response, "payload", cb, 1, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected void invokeCallback(String channel, String response, String key, Callback callback, int extendedErrorCode,
                                  Result result) {
        invokeCallback(channel, response, key, callback, extendedErrorCode, false, result);
    }

    protected void invokeCallback(String channel, String response, String key, Callback callback, int extendedErrorCode,
                                  boolean key_strict, Result result) {
        JSONObject responseJso = null;
        try {
            responseJso = new JSONObject(response);
        } catch (JSONException e) {
            callback.errorCallback(channel,
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_JSON_ERROR, extendedErrorCode, response),
                    result);
            return;
        }

        JSONObject payloadJso = null;

        if (key != null && key.length() > 0) {
            try {
                payloadJso = (JSONObject) responseJso.get(key);
            } catch (JSONException e) {
                if (!key_strict) {
                    callback.successCallback(channel, responseJso, result);
                } else {
                    callback.errorCallback(channel,
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_JSON_ERROR, extendedErrorCode, response)
                            , result);
                }
                return;

            }
            callback.successCallback(channel, payloadJso, result);
            return;
        }
    }





    protected void invokeJSONStringCallback(String response, String key, Callback callback, Result result) {
        String responseJSON;

        try {
            responseJSON = (new JSONObject(response)).getString(key);
            callback.successCallback(null, responseJSON, result);
        } catch (JSONException e) {
            callback.errorCallback(null, PubnubError.getErrorObject(PubnubError.PNERROBJ_JSON_ERROR,
                    0, response), result);
        }
    }

    protected Object _channelGroupRemoveNamespace(String namespace, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);

        Result result = new Result();
        
        String[] url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                "namespace", namespace, "remove" };

        Hashtable parameters = PubnubUtil.hashtableClone(params);

        HttpRequest hreq = new HttpRequest(url, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeJSONStringCallback(response, "message", cb, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _channelGroupListGroups(String namespace, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        String[] url;

        Result result = new Result();
        
        if (namespace != null) {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "namespace", namespace, "channel-group" };
        } else {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "channel-group" };
        }

        Hashtable parameters = PubnubUtil.hashtableClone(params);

        HttpRequest hreq = new HttpRequest(url, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback("", response, "payload", cb, 0, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _channelGroupListChannels(String group, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        ChannelGroup channelGroup;
        String[] url;
        
        Result result = new Result();

        try {
            channelGroup = new ChannelGroup(group);
        } catch (PubnubException e) {
            cb.errorCallback(null, PubnubError.PNERROBJ_CHANNEL_GROUP_PARSING_ERROR, result);
            return null;
        }

        if (channelGroup.namespace != null) {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "namespace", channelGroup.namespace, "channel-group", channelGroup.group };
        } else {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "channel-group", channelGroup.group };
        }

        Hashtable parameters = PubnubUtil.hashtableClone(params);

        HttpRequest hreq = new HttpRequest(url, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback("", response, "payload", cb, 0, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _channelGroupUpdate(String action, String group, String[] channels, final Callback callback,
            boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        ChannelGroup channelGroup;
        String[] url;

        Result result = new Result();
        
        try {
            channelGroup = new ChannelGroup(group);
        } catch (PubnubException e) {
            cb.errorCallback(null, PubnubError.PNERROBJ_CHANNEL_GROUP_PARSING_ERROR, result);
            return null;
        }

        if (channelGroup.namespace != null) {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "namespace", channelGroup.namespace, "channel-group", channelGroup.group };
        } else {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "channel-group", channelGroup.group };
        }

        Hashtable parameters = PubnubUtil.hashtableClone(params);

        if (channels.length > 0) {
            parameters.put(action, PubnubUtil.joinString(channels, ","));
        }

        HttpRequest hreq = new HttpRequest(url, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeJSONStringCallback(response, "message", cb, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }

        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _channelGroupRemoveGroup(String group, Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        ChannelGroup channelGroup;
        String[] url;

        Result result = new Result();
        
        try {
            channelGroup = new ChannelGroup(group);
        } catch (PubnubException e) {
            cb.errorCallback(null, PubnubError.PNERROBJ_CHANNEL_GROUP_PARSING_ERROR, result);
            return null;
        }

        if (channelGroup.namespace != null) {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "namespace", channelGroup.namespace, "channel-group", channelGroup.group, "remove" };
        } else {
            url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                    "channel-group", channelGroup.group, "remove" };
        }

        Hashtable parameters = PubnubUtil.hashtableClone(params);

        HttpRequest hreq = new HttpRequest(url, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeJSONStringCallback(response, "message", cb, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }

        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _channelGroupListNamespaces(Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);

        Result result = new Result();
        
        String[] url = new String[] { getPubnubUrl(result), "v1", "channel-registration", "sub-key", this.SUBSCRIBE_KEY,
                "namespace" };

        Hashtable parameters = PubnubUtil.hashtableClone(params);

        HttpRequest hreq = new HttpRequest(url, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback("", response, "payload", cb, 0, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(null, error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _disablePushNotificationsOnChannels(final String[] channels, String gcmRegistrationId,
            final Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        
        Result result = new Result();

        Hashtable parameters = PubnubUtil.hashtableClone(params);
        String[] urlargs = null;
        urlargs = new String[] { getPubnubUrl(result), "v1", "push", "sub-key", this.SUBSCRIBE_KEY, "devices",
                gcmRegistrationId };

        parameters.put("type", "gcm");
        parameters.put("remove", PubnubUtil.joinString(channels, ","));

        HttpRequest hreq = new HttpRequest(urlargs, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                JSONArray jsarr;
                try {
                    jsarr = new JSONArray(response);
                } catch (JSONException e) {
                    handleError(hreq, 
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_JSON, 1, response)
                            , result);
                    return;
                }
                cb.successCallback("", jsarr, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
                return;
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _requestPushNotificationEnabledChannelsForDeviceRegistrationId(String gcmRegistrationId,
            final Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        
        Result result = new Result();
        
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        String[] urlargs = null;
        urlargs = new String[] { getPubnubUrl(result), "v1", "push", "sub-key", this.SUBSCRIBE_KEY, "devices",
                gcmRegistrationId };

        parameters.put("type", "gcm");

        HttpRequest hreq = new HttpRequest(urlargs, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                JSONArray jsarr;
                try {
                    jsarr = new JSONArray(response);
                } catch (JSONException e) {
                    handleError(hreq, 
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_JSON, 1, response)
                            , result);
                    return;
                }
                cb.successCallback("", jsarr, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
                return;
            }
        }, result);
        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _removeAllPushNotificationsForDeviceRegistrationId(String gcmRegistrationId,
            final Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        
        Result result = new Result();
        
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        String[] urlargs = null;
        urlargs = new String[] { getPubnubUrl(result), "v1", "push", "sub-key", this.SUBSCRIBE_KEY, "devices",
                gcmRegistrationId, "remove" };

        parameters.put("type", "gcm");

        HttpRequest hreq = new HttpRequest(urlargs, parameters, new ResponseHandler() {
            @Override
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                JSONArray jsarr;
                try {
                    jsarr = new JSONArray(response);
                } catch (JSONException e) {
                    handleError(hreq, 
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_JSON, 1, response)
                            , result);
                    return;
                }
                cb.successCallback("", jsarr, result);
            }
            @Override
            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
                return;
            }
        }, result);
        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _enablePushNotificationsOnChannels(final String[] channels, String gcmRegistrationId,
            final Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);

        Result result = new Result();
        
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        String[] urlargs = null;
        urlargs = new String[] { getPubnubUrl(result), "v1", "push", "sub-key", this.SUBSCRIBE_KEY, "devices",
                gcmRegistrationId };

        parameters.put("type", "gcm");
        parameters.put("add", PubnubUtil.joinString(channels, ","));

        HttpRequest hreq = new HttpRequest(urlargs, parameters, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                JSONArray jsarr;
                try {
                    jsarr = new JSONArray(response);
                } catch (JSONException e) {
                    handleError(hreq, 
                            PubnubError.getErrorObject(PubnubError.PNERROBJ_INVALID_JSON, 1, response)
                            , result);
                    return;
                }
                cb.successCallback("", jsarr, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback("", error, result);
                return;
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected String pamSign(String key, String data) throws PubnubException {
        return null;
    }

    protected Object _pamAuditChannelGroup(final String group, String auth_key, Callback callback, boolean sync) {
        String signature;
        final Callback cb = getWrappedCallback(callback);
        
        Result result = new Result();

        Hashtable parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            cb.errorCallback(group, 
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_SECRET_KEY_MISSING, 3), result);
            return null;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n";

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + auth_key + "&";

        sign_input += "channel-group=" + PubnubUtil.urlEncode(group) + "&" + "pnsdk="
                + PubnubUtil.urlEncode(getUserAgent()) + "&" + "timestamp=" + timestamp;

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(group, e1.getPubnubError(), result);
            return null;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("channel-group", group);

        if (auth_key != null && auth_key.length() > 0)
            parameters.put("auth", auth_key);

        String[] urlComponents = { getPubnubUrl(result), "v1", "auth", "audit", "sub-key", this.SUBSCRIBE_KEY };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback(group, response, "payload", cb, 6, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(group, error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _pamAudit(final String channel, String auth_key, Callback callback, boolean sync) {

        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        Result result = new Result();
        
        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel, 
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_SECRET_KEY_MISSING, 4), result);
            return null;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n" + "auth="
                + PubnubUtil.urlEncode(auth_key) + "&" + "channel=" + PubnubUtil.urlEncode(channel) + "&" + "pnsdk="
                + PubnubUtil.urlEncode(getUserAgent()) + "&" + "timestamp=" + timestamp;

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel, e1.getPubnubError(), result);
            return null;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("channel", channel);
        parameters.put("auth", auth_key);

        String[] urlComponents = { getPubnubUrl(result), "v1", "auth", "audit", "sub-key", this.SUBSCRIBE_KEY };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback(channel, response, "payload", cb, 2, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(channel, error, result);
                return;
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);

    }

    protected Object _pamAudit(final String channel, Callback callback, boolean sync) {

        final Callback cb = getWrappedCallback(callback);
        
        Result result = new Result();

        Hashtable parameters = PubnubUtil.hashtableClone(params);
        parameters.remove("auth");

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel, 
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_SECRET_KEY_MISSING, 3), result);
            return null;
        }
        String sign_input = null;
        if (channel != null) {
            sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n" + "channel="
                    + PubnubUtil.pamEncode(channel) + "&" + "pnsdk=" + PubnubUtil.pamEncode(getUserAgent()) + "&"
                    + "timestamp=" + timestamp;
        } else {
            sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "audit" + "\n" + "pnsdk="
                    + PubnubUtil.pamEncode(getUserAgent()) + "&" + "timestamp=" + timestamp;
        }

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel, e1.getPubnubError(), result);
            return null;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        if (channel != null)
            parameters.put("channel", channel);

        String[] urlComponents = { getPubnubUrl(result), "v1", "auth", "audit", "sub-key", this.SUBSCRIBE_KEY };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback(channel, response, "payload", cb, 6, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(channel, error, result);
                return;
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);

    }

    protected Object _pamGrantChannelGroup(final String group, String auth_key, boolean read, boolean management,
            int ttl, Callback callback, boolean sync) {
        String signature;
        Result result = new Result();
        
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);

        String r = (read) ? "1" : "0";
        String m = (management) ? "1" : "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(group, 
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_SECRET_KEY_MISSING, 1),
                    result);
            return null;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "grant" + "\n";

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + PubnubUtil.pamEncode(auth_key) + "&";

        sign_input += "channel-group=" + PubnubUtil.pamEncode(group) + "&" + "m=" + m + "&" + "pnsdk="
                + PubnubUtil.pamEncode(getUserAgent()) + "&" + "r=" + r + "&" + "timestamp=" + timestamp
                + ((ttl >= -1) ? "&" + "ttl=" + ttl : "");

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(group, e1.getPubnubError(), result);
            return null;
        }

        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("r", r);
        parameters.put("m", m);
        parameters.put("channel-group", group);

        if (ttl >= -1)
            parameters.put("ttl", String.valueOf(ttl));
        if (auth_key != null && auth_key.length() > 0)
            parameters.put("auth", auth_key);

        String[] urlComponents = { getPubnubUrl(result), "v1", "auth", "grant", "sub-key", this.SUBSCRIBE_KEY };

        HttpRequest hreq = new HttpRequest(urlComponents, parameters, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback(group, response, "payload", cb, 4, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(group, error, result);
            }
        }, result);

        return _request(hreq, (sync) ? null : nonSubscribeManager);
    }

    protected Object _pamGrant(final String channel, String auth_key, boolean read, boolean write, int ttl,
            Callback callback, boolean sync) {
        final Callback cb = getWrappedCallback(callback);
        Hashtable parameters = PubnubUtil.hashtableClone(params);
        final GrantResult result = new GrantResult();
        parameters.remove("auth");

        String r = (read) ? "1" : "0";
        String w = (write) ? "1" : "0";

        String signature = "0";

        int timestamp = (int) ((new Date().getTime()) / 1000);

        if (this.SECRET_KEY.length() == 0) {
            callback.errorCallback(channel, 
                    PubnubError.getErrorObject(PubnubError.PNERROBJ_SECRET_KEY_MISSING, 1), result);
            return null;
        }

        String sign_input = this.SUBSCRIBE_KEY + "\n" + this.PUBLISH_KEY + "\n" + "grant" + "\n";

        if (auth_key != null && auth_key.length() > 0)
            sign_input += "auth=" + PubnubUtil.pamEncode(auth_key) + "&";

        sign_input += "channel=" + PubnubUtil.pamEncode(channel) + "&" + "pnsdk="
                + PubnubUtil.pamEncode(getUserAgent()) + "&" + "r=" + r + "&" + "timestamp=" + timestamp
                + ((ttl >= -1) ? "&" + "ttl=" + ttl : "") + "&" + "w=" + w;

        try {
            signature = pamSign(this.SECRET_KEY, sign_input);
        } catch (PubnubException e1) {
            callback.errorCallback(channel, e1.getPubnubError(), result);
            return null;
        }

        parameters.put("w", w);
        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("signature", signature);
        parameters.put("r", r);
        parameters.put("channel", channel);

        if (auth_key != null && auth_key.length() > 0)
            parameters.put("auth", auth_key);
        if (ttl >= -1)
            parameters.put("ttl", String.valueOf(ttl));

        String[] urlComponents = { getPubnubUrl(result), "v1", "auth", "grant", "sub-key", this.SUBSCRIBE_KEY };


        HttpRequest hreq = new HttpRequest(urlComponents, parameters, new ResponseHandler() {
            public void handleResponse(HttpRequest hreq, String response, Result result) {
                invokeCallback(channel, response, "payload", cb, 4, result);
            }

            public void handleError(HttpRequest hreq, PubnubError error, Result result) {
                cb.errorCallback(channel, error, result);
                return;
            }
        }, result);

        setResultData(result, OperationType.PAM_MODIFY, hreq);
        return _request(hreq, (sync) ? null : nonSubscribeManager);

    }

}
