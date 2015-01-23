package com.pubnub.api.datasync;

import com.pubnub.api.*;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class NextPageTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);

        playerString = "player-" + TestHelper.random();
    }

    @Test
    public void testNextPage() throws InterruptedException, JSONException, IllegalAccessException, NoSuchFieldException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);

        Field f = pubnub.getClass().getSuperclass().getSuperclass().getDeclaredField("params");
        f.setAccessible(true);
        Hashtable<String, String> params = (Hashtable<String, String>) f.get(pubnub);

        params.put("page_max_bytes", "10");

        final CountDownLatch latch = new CountDownLatch(1);

        DataSyncCallback settingsCallback = new DataSyncCallback() {
            @Override
            public void readyCallback() {
                latch.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, settingsCallback);

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount());
        assertEquals(new Integer(3), player.size("settings"));
        assertEquals(new Integer(3), player.size("tracks"));
    }
}
