package com.pubnub.api.datasync;

import com.pubnub.api.*;
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

    @Test
    public void testRemoveByIndex() throws InterruptedException, JSONException {
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

        String track = (String) tracks.removeByIndex(1);
        assertEquals("track#2", track);

        latch2.await(5, TimeUnit.SECONDS);

        assertEquals(2, tracks.getList().size());
        assertEquals("track#3", tracks.getByIndex(1));
        assertEquals(0, latch1.getCount());
        assertEquals(0, latch2.getCount());
    }

    @Test
    public void testReplaceByIndex() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                latch1.countDown();
            }

            @Override
            public void replaceCallback(List updates, String path) {
                result = updates;
                latch2.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        SyncedObject tracks = player.child("tracks");
        String track2 = (String) tracks.replaceByIndex(1, "hi");
        assertEquals("track#2", track2);

        latch2.await(5, TimeUnit.SECONDS);
        List updates = (List) cb1.getResult();
        assertEquals(1, updates.size());
        assertEquals("hi", ((SyncedObjectDelta) updates.get(0)).getValue());
        assertEquals("hi", tracks.getByIndex(1));

        assertEquals(Integer.valueOf(3), tracks.size());
        assertEquals(0, latch1.getCount());
    }

    @Test
    public void testRemoveByKey() throws InterruptedException, JSONException {
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
        String key = tracks.getKeyByIndex(2);

        String track = (String) tracks.removeByKey(key);
        assertEquals("track#3", track);

        latch2.await(5, TimeUnit.SECONDS);

        assertEquals(2, tracks.getList().size());
        assertEquals("track#2", tracks.getByIndex(1));
        assertEquals(0, latch1.getCount());
        assertEquals(0, latch2.getCount());
    }

    @Test
    public void testReplaceByKey() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                latch1.countDown();
            }

            @Override
            public void replaceCallback(List updates, String path) {
                result = updates;
                latch2.countDown();
            }
        };

        SyncedObject player = pubnub.sync(playerString, cb1);

        latch1.await(5, TimeUnit.SECONDS);

        SyncedObject tracks = player.child("tracks");
        String key = tracks.getKeyByIndex(2);
        String track2 = (String) tracks.replaceByKey(key, "hi");
        assertEquals("track#3", track2);

        latch2.await(5, TimeUnit.SECONDS);
        List updates = (List) cb1.getResult();
        assertEquals(1, updates.size());
        assertEquals("hi", ((SyncedObjectDelta) updates.get(0)).getValue());
        assertEquals("hi", tracks.getByIndex(2));

        assertEquals(Integer.valueOf(3), tracks.size());
        assertEquals(0, latch1.getCount());
    }
}
