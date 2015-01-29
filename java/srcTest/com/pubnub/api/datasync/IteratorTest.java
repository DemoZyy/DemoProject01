package com.pubnub.api.datasync;

import com.pubnub.api.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class IteratorTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");
        playerString = "player-" + TestHelper.random();

        pubnub.setCacheBusting(false);
    }

    @Test
    public void testObjectIterator() throws InterruptedException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);
        ArrayList<String> locations = new ArrayList<String>();
        locations.add(playerString + ".settings.locale");
        locations.add(playerString + ".settings.mute");
        locations.add(playerString + ".settings.volume");

        final CountDownLatch latch1 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb = new TestHelper.SimpleDataSyncCallback(latch1);

        SyncedObject settings = pubnub.sync(playerString + ".settings", cb);

        latch1.await(5, TimeUnit.SECONDS);

        Iterator iterator = settings.iterator();
        SyncedObject current;

        while (iterator.hasNext()) {
            current = (SyncedObject) iterator.next();
            locations.remove(current.getLocation());
        }

        assertEquals(0, locations.size());
    }

    @Test
    public void testListIterator() throws InterruptedException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);
        ArrayList<String> trackStrings = new ArrayList<String>();

        trackStrings.add("track#1");
        trackStrings.add("track#2");
        trackStrings.add("track#3");

        final CountDownLatch latch1 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb = new TestHelper.SimpleDataSyncCallback(latch1);

        SyncedObject tracks = pubnub.sync(playerString + ".tracks", cb);

        latch1.await(5, TimeUnit.SECONDS);

        Iterator iterator = tracks.iterator();
        SyncedObject current;

        while (iterator.hasNext()) {
            current = (SyncedObject) iterator.next();
            trackStrings.remove(current.optString(""));
        }

        assertEquals(0, trackStrings.size());
    }

    @Test
    public void testStringIterator() throws InterruptedException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub, true);
        final CountDownLatch latch1 = new CountDownLatch(1);

        TestHelper.SimpleDataSyncCallback cb = new TestHelper.SimpleDataSyncCallback(latch1);

        SyncedObject locale = pubnub.sync(playerString + ".settings.locale", cb);

        latch1.await(5, TimeUnit.SECONDS);

        Iterator iterator = locale.iterator();

        assertNull(iterator);
    }
}
