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

public class Pubnub extends PubnubCoreShared {
    
    public Pubnub() {
       
    }

    public static class Build {

    };

    protected String getUserAgent() {
        return "Java/" + VERSION;
    }

}
