package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.SyncedObject;
import com.pubnub.api.TestHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DataSyncTestHelper {
    static JSONObject rawPlayer;
    static JSONObject rawChildren;

    static JSONObject expectedPlayer;
    static JSONObject expectedChildren;

    private static JSONObject player;

    static {
        try {
            rawPlayer = new JSONObject("{\"music\": {\"artists\": {\"-!14172480320903800\": {\"last\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Sinatra\"}, \"first\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Frank\"}}, \"-!14172480321030890\": {\"last\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Dylan\"}, \"first\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Bob\"}}}}, 'i':123, 'b':true, 's': 'abc'}");
            rawChildren = new JSONObject("{\"-a!14189386794825700\": {\"pn_tt\": \"14189386794826050\", \"pn_val\": \"alice\"}, \"-z!14189386904427630\": {\"pn_tt\": \"14189386904428010\", \"pn_val\": \"bob\"}, \"-n!14189387080996980\": {\"pn_tt\": \"14189387080997350\", \"pn_val\": \"james\"}}");

            expectedPlayer = new JSONObject("{\"music\":{\"artists\":[{\"last\":\"Sinatra\",\"first\":\"Frank\"},{\"last\":\"Dylan\",\"first\":\"Bob\"}]}}");
            expectedChildren = new JSONObject("{\"children\": [\"alice\",\"bob\",\"james\"]}");

            player = new JSONObject();
            {
                JSONObject settings = new JSONObject();
                settings.put("volume", 70);
                settings.put("mute", true);
                settings.put("locale", "en");

                player.put("settings", settings);

                JSONArray tracks = new JSONArray();

                tracks.put("track#1");
                tracks.put("track#2");
                tracks.put("track#3");

                player.put("tracks", tracks);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getPlayer() {
        try {
            return new JSONObject(player.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setupSettingsOn(String objectId, Pubnub pubnub) {
        setupSettingsOn(objectId, pubnub, false);
    }

    public static void setupSettingsOn(String objectId, Pubnub pubnub, Boolean withTracks) {
        final CountDownLatch latch = new CountDownLatch(1);
        TestHelper.SimpleDataSyncCallback cb = new TestHelper.SimpleDataSyncCallback(latch);

        Hashtable<String, Object> args = new Hashtable<String, Object>();

        JSONObject playerCopy = getPlayer();

        if (!withTracks) {
            playerCopy.remove("tracks");
        }

        args.put("location", objectId);
        args.put("data", playerCopy);

        pubnub.merge(args, new TestHelper.SimpleCallback());
        SyncedObject player = pubnub.sync(objectId, cb);

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player.remove();
    }
}
