package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SyncedObjectValueGettersTest {
    Pubnub pubnub;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);
    }

    @Test
    public void testGetValue() throws InterruptedException, JSONException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(2);
        final CountDownLatch latch3 = new CountDownLatch(1);

        TestHelper.SimpleCallback cb = new TestHelper.SimpleCallback(latch1);
        TestHelper.SimpleDataSyncCallback cb2 = new TestHelper.SimpleDataSyncCallback(latch2) {
            @Override
            public void mergeCallback(List updates, String path) {
                latch.countDown();
            }
        };
        TestHelper.SimpleDataSyncCallback cb3 = new TestHelper.SimpleDataSyncCallback(latch3);

        String playerObjectName = "player-test";
        String location = playerObjectName + ".settings";

        JSONObject localSettings = new JSONObject();

        localSettings.put("volume", 73);
        localSettings.put("position", "22");
        localSettings.put("mute", false);
        localSettings.put("locale", "en");

        Hashtable<String, Object> args = new Hashtable<String, Object>();

        args.put("location", location);
        args.put("data", localSettings);

        SyncedObject player = pubnub.sync(playerObjectName, cb2);
        pubnub.merge(args, cb);
        latch2.await(5, TimeUnit.SECONDS);

        // 2nd level
        assertEquals("en", player.getString("settings.locale"));
        assertEquals("73", player.getString("settings.volume"));
        assertEquals(new Integer(73), player.getInteger("settings.volume"));
        assertEquals("22", player.getString("settings.position"));
        assertEquals(new Integer(22), player.getInteger("settings.position"));
        assertFalse(player.getBoolean("settings.mute"));

        assertEquals(4, player.getMap("settings").size());
        assertEquals("en", player.getMap("settings").get("locale"));
        assertEquals(73, player.getMap("settings").get("volume"));

        SyncedObject settings = player.child("settings", cb3);
        latch3.await(5, TimeUnit.SECONDS);

        // 1st level
        assertEquals("en", settings.getString("locale"));
        assertEquals("73", settings.getString("volume"));
        assertEquals(new Integer(73), settings.getInteger("volume"));
        assertEquals("22", settings.getString("position"));
        assertEquals(new Integer(22), settings.getInteger("position"));
        assertFalse(settings.getBoolean("mute"));

        assertEquals(4, settings.getMap().size());
        assertEquals("en", settings.getMap().get("locale"));
        assertEquals(73, settings.getMap().get("volume"));

        assertEquals(0, latch1.getCount());
        assertEquals(0, latch2.getCount());
        assertEquals(0, latch3.getCount());
    }
}
