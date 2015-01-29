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

public class RemoveTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);

        playerString = "player" + TestHelper.random();
    }

    @Test
    public void testRemoveResponse() throws InterruptedException, JSONException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        TestHelper.SimpleCallback cb1 = new TestHelper.SimpleCallback(latch1);
        TestHelper.SimpleCallback cb2 = new TestHelper.SimpleCallback(latch2);

        Hashtable mergeArgs = new Hashtable();
        mergeArgs.put("location", "player.settings.volume");
        mergeArgs.put("data", 70);

        pubnub.merge(mergeArgs, cb1);
        latch1.await(5, TimeUnit.SECONDS);


        Hashtable removeArgs = new Hashtable();
        removeArgs.put("location", "player.settings.volume");

        pubnub.remove(removeArgs, cb2);
        latch2.await(5, TimeUnit.SECONDS);

        JSONObject response = (JSONObject) cb2.getResponse();
        System.out.println(response.toString());
        assertEquals("settings.volume", response.getString("location"));
        assertEquals("delete", response.getString("op"));
        assertEquals(0, latch2.getCount());
    }

    @Test
    public void testRemoveObjectCallback() throws InterruptedException, JSONException, PubnubException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub);

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback(latch1) {
            @Override
            public void removeCallback(List updates, String path) {
                latch2.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        assertEquals(70, player.getInteger("settings.volume"));
        assertEquals("en", player.getString("settings.locale"));
        assertTrue(player.getBoolean("settings.mute"));

        player.remove("settings.volume");

        latch2.await(5, TimeUnit.SECONDS);

        assertEquals(0, player.optInteger("settings.volume"));
        assertEquals("en", player.getString("settings.locale"));
        assertTrue(player.getBoolean("settings.mute"));

        assertEquals(0, latch1.getCount());
    }
}
