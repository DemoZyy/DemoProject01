package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.TestHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DataSyncTestHelper {
    public static void setupSettingsOn(String objectId, Pubnub pubnub) {
        setupSettingsOn(objectId, pubnub, false);
    }

    public static void setupSettingsOn(String objectId, Pubnub pubnub, Boolean withTracks) {
        final CountDownLatch latch = new CountDownLatch(2);
        TestHelper.SimpleDataSyncCallback cb = new TestHelper.SimpleDataSyncCallback(latch) {
            @Override
            public void mergeCallback(List updates, String path) {
                latch.countDown();
            }
        };

        Hashtable<String, Object> args = new Hashtable<String, Object>();
        JSONObject player = new JSONObject();
        JSONObject settings = new JSONObject();

        try {
            settings.put("volume", 70);
            settings.put("mute", true);
            settings.put("locale", "en");

            player.put("settings", settings);

            if (withTracks) {
                JSONArray tracks = new JSONArray();

                tracks.put("track#1");
                tracks.put("track#2");
                tracks.put("track#3");

                player.put("tracks", tracks);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        args.put("location", objectId);
        args.put("data", player);

        pubnub.merge(args, new TestHelper.SimpleCallback());
        pubnub.sync(objectId, cb);

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
