package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.SyncedObject;
import com.pubnub.api.TestHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MergeTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);

        playerString = "player-" + TestHelper.random();
    }

    @Test
    public void testMergeResponse() throws InterruptedException, JSONException {
        final CountDownLatch latch = new CountDownLatch(1);
        TestHelper.SimpleCallback cb = new TestHelper.SimpleCallback(latch);

        Hashtable<String, Object> args = new Hashtable<String, Object>();

        args.put("location", "player.settings.volume");
        args.put("data", 70);

        pubnub.merge(args, cb);

        latch.await(5, TimeUnit.SECONDS);

        JSONObject response = (JSONObject) cb.getResponse();

        assertEquals("settings.volume", response.getString("location"));
        assertEquals("merge", response.getString("op"));
        assertEquals(0, latch.getCount());
    }

    @Test
    public void testMergeObjectResponse() throws InterruptedException, JSONException {
        final CountDownLatch latch = new CountDownLatch(1);
        TestHelper.SimpleCallback cb = new TestHelper.SimpleCallback(latch);

        JSONObject settings = new JSONObject();

        settings.put("volume", 70);
        settings.put("mute", false);
        settings.put("locale", "en");

        Hashtable<String, Object> args = new Hashtable<String, Object>();

        args.put("location", "player.settings");
        args.put("data", settings);

        pubnub.merge(args, cb);

        latch.await(5, TimeUnit.SECONDS);

        JSONObject response = (JSONObject) cb.getResponse();

        assertEquals("settings", response.getString("location"));
        assertEquals("merge", response.getString("op"));
        assertEquals(0, latch.getCount());
    }

    @Test
    public void testMergeObjectCallback() throws InterruptedException, JSONException, PubnubException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub);

        final CountDownLatch latch1 = new CountDownLatch(3);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                JSONObject settings = new JSONObject();

                try {
                    settings.put("volume", 70);
                    settings.put("mute", false);
                } catch (JSONException e) {
                    fail(e.getMessage());
                }

                syncedObject.merge("settings", settings);
                latch1.countDown();
            }

            @Override
            public void changeCallback(List updates, String path) {
                latch1.countDown();
            }

            @Override
            public void mergeCallback(List updates, String path) {
                result = updates;
                latch1.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        List updates = (List) cb1.getResult();
        assertEquals(2, updates.size());

        assertEquals(70, player.getInteger("settings.volume"));
        assertEquals("en", player.getString("settings.locale"));
        assertFalse(player.getBoolean("settings.mute"));

        SyncedObject settings = player.child("settings");

        assertEquals(70, settings.getInteger("volume"));
        assertEquals("en", settings.getString("locale"));
        assertFalse(settings.getBoolean("mute"));

        assertEquals(0, latch1.getCount());
    }
}
