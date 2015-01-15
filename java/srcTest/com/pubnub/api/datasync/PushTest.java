package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.SyncedObject;
import com.pubnub.api.TestHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PushTest {
    Pubnub pubnub;
    String playerString;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);

        playerString = "player" + TestHelper.random();
    }

    @Test
    public void testPushResponse() throws InterruptedException, JSONException {
        final CountDownLatch latch = new CountDownLatch(1);
        TestHelper.SimpleCallback cb = new TestHelper.SimpleCallback(latch);

        Hashtable<String, Object> args = new Hashtable<String, Object>();

        args.put("location", "player.settings.skins_list");
        args.put("data", 70);

        pubnub.push(args, cb);

        latch.await(5, TimeUnit.SECONDS);

        JSONObject response = (JSONObject) cb.getResponse();

        assertEquals("settings.skins_list", response.getString("location"));
        assertEquals("push", response.getString("op"));
        assertEquals(0, latch.getCount());
    }

    @Test
    public synchronized void testPushWholeObjectCallback() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub);

        final CountDownLatch latch1 = new CountDownLatch(2);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                JSONArray tracks = new JSONArray();

                tracks.put("track#1");
                tracks.put("track#2");
                tracks.put("track#3");

                syncedObject.push("tracks", tracks);
                latch1.countDown();
            }

            @Override
            public void mergeCallback(List updates, String path) {
                result = updates;
                latch1.countDown();
            }
        };

        pubnub.sync(playerString, cb1);

        latch1.await(30, TimeUnit.SECONDS);

        List updates = (List) cb1.getResult();
        assertEquals(3, updates.size());

        assertEquals(0, latch1.getCount());
    }

    @Test
    public void testPushObjectItemsCallback() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub);

        final CountDownLatch latch1 = new CountDownLatch(4);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                syncedObject.push("tracks", "track#1");
                syncedObject.push("tracks", "track#2");
                syncedObject.push("tracks", "track#3");

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

        List updates = player.getList("tracks");
        assertEquals(3, updates.size());
        assertEquals("track#1", updates.get(0));
        assertEquals("track#2", updates.get(1));
        assertEquals("track#3", updates.get(2));

        assertEquals(0, latch1.getCount());
    }

    @Test
    public void testPushSortedObject() throws InterruptedException, JSONException {
        DataSyncTestHelper.setupSettingsOn(playerString, pubnub);

        final CountDownLatch latch1 = new CountDownLatch(4);

        TestHelper.SimpleDataSyncCallback cb1 = new TestHelper.SimpleDataSyncCallback() {
            @Override
            public void readyCallback(SyncedObject syncedObject) {
                syncedObject.push("tracks", "track#1", "a");
                syncedObject.push("tracks", "track#2", "z");
                syncedObject.push("tracks", "track#3", "n");

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

        List updates = player.getList("tracks");
        assertEquals(3, updates.size());
        assertEquals("track#1", updates.get(0));
        assertEquals("track#3", updates.get(1));
        assertEquals("track#2", updates.get(2));

        assertEquals(0, latch1.getCount());
    }
}
