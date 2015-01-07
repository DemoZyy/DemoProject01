package com.pubnub.api.datasync;

import com.pubnub.api.DataSyncCallback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.SyncedObject;
import com.pubnub.api.TestHelper;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ListMethodsTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);
        playerString = "player-" + TestHelper.random();
    }

    @Test
    public void testPopList() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        DataSyncCallback cb1 = new DataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                latch1.countDown();
            }

            @Override
            public void removeCallback(List updates, String path) {
                latch2.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        SyncedObject tracks = player.child("tracks");
        assertEquals(3, tracks.getList().size());

        String track = (String) tracks.pop();
        assertEquals("track#3", track);

        latch2.await(5, TimeUnit.SECONDS);

        assertEquals(2, tracks.getList().size());
        assertEquals(0, latch1.getCount());
        assertEquals(0, latch2.getCount());
    }

    @Test
    public void testShiftList() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        DataSyncCallback cb1 = new DataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                latch1.countDown();
            }

            @Override
            public void removeCallback(List updates, String path) {
                latch2.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        SyncedObject tracks = player.child("tracks");
        assertEquals(3, tracks.getList().size());

        String track = (String) tracks.shift();
        assertEquals("track#1", track);

        latch2.await(5, TimeUnit.SECONDS);

        assertEquals(2, tracks.getList().size());
        assertEquals(0, latch1.getCount());
        assertEquals(0, latch2.getCount());
    }
}
