package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.SyncedObject;
import com.pubnub.api.TestHelper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InitialValueTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException, JSONException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);

        playerString = "player-" + TestHelper.random();

        DataSyncTestHelper.setupSettingsOn(playerString, pubnub);
    }

    @Test
    public void testMergeObjectCallback() throws InterruptedException, JSONException, PubnubException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback(latch1);
        TestHelper.SimpleDataSyncCallback cb2 = new TestHelper.SimpleDataSyncCallback(latch2);

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        assertEquals(70, player.getInteger("settings.volume"));
        assertTrue(player.getBoolean("settings.mute"));

        SyncedObject settings = player.child("settings", cb2);

        assertEquals(70, settings.getInteger("volume"));
        assertTrue(settings.getBoolean("mute"));

        assertEquals(0, latch1.getCount());
    }

    // TODO: add recursive loader test
}
