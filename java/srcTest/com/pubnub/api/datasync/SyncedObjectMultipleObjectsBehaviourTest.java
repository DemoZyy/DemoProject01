package com.pubnub.api.datasync;

import com.pubnub.api.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SyncedObjectMultipleObjectsBehaviourTest {
    Pubnub pubnub;
    SyncedObjectManager syncedObjectManager;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");
        syncedObjectManager = new SyncedObjectManager(pubnub);

        pubnub.setCacheBusting(false);
    }

    @Test
    public void testSubscribeRootAfterChildren() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        final CountDownLatch settingsLatch = new CountDownLatch(1);
        final CountDownLatch playerLatch = new CountDownLatch(1);
        final CountDownLatch playersLatch = new CountDownLatch(1);

        DataSyncCallback settingsCallback = new DataSyncCallback() {
            @Override
            public void mergeCallback(List updates, String path) {
                settingsLatch.countDown();
            }
        };

        DataSyncCallback playerCallback = new DataSyncCallback() {
            @Override
            public void mergeCallback(List updates, String path) {
                playerLatch.countDown();
            }
        };

        DataSyncCallback playersCallback = new DataSyncCallback() {
            @Override
            public void mergeCallback(List updates, String path) {
                playersLatch.countDown();
            }
        };

        SyncedObject player = pubnub.sync("player", playerCallback);
        pubnub.sync("player.settings", settingsCallback);
        pubnub.sync("players", playersCallback);

        player.merge("settings.color", "red");
        settingsLatch.await(5, TimeUnit.SECONDS);
        playerLatch.await(5, TimeUnit.SECONDS);

        assertEquals(0, settingsLatch.getCount());
        assertEquals(0, playerLatch.getCount());
        assertEquals(1, playersLatch.getCount());
    }

    @Test
    public void testUnsubscribe()
            throws NoSuchFieldException, IllegalAccessException, InterruptedException, NoSuchMethodException, InvocationTargetException {
        final CountDownLatch settingsLatch = new CountDownLatch(1);
        final CountDownLatch playerLatch = new CountDownLatch(1);
        final CountDownLatch playersLatch = new CountDownLatch(1);

        DataSyncCallback settingsCallback = new DataSyncCallback() {
            @Override
            public void readyCallback() {
                settingsLatch.countDown();
            }
        };

        DataSyncCallback playerCallback = new DataSyncCallback() {
            @Override
            public void readyCallback() {
                playerLatch.countDown();
            }
        };

        DataSyncCallback playersCallback = new DataSyncCallback() {
            @Override
            public void readyCallback() {
                playersLatch.countDown();
            }
        };

        SyncedObject player = pubnub.sync("player", playerCallback);
        pubnub.sync("player.settings", settingsCallback);
        pubnub.sync("players", playersCallback);

        settingsLatch.await(5, TimeUnit.SECONDS);
        playerLatch.await(5, TimeUnit.SECONDS);
        playersLatch.await(5, TimeUnit.SECONDS);

        player.unsubscribe();

        Field f = pubnub.getClass().getSuperclass().getSuperclass().getDeclaredField("syncedObjectManager");
        f.setAccessible(true);
        SyncedObjectManagerCore manager = (SyncedObjectManager) f.get(pubnub);

        Method m = manager.getClass().getSuperclass().getDeclaredMethod("getChannelsForSubscribe");
        m.setAccessible(true);

        String[] channels = (String[]) m.invoke(manager);
        assertEquals(6, channels.length);
        assertFalse(Arrays.asList(channels).contains("pn_ds_player.*"));
        assertFalse(Arrays.asList(channels).contains("pn_ds_player"));
    }

    @Test
    public void testInnerLevelSyncedObjectInstantiation() {
        SyncedObject artists = new SyncedObject(syncedObjectManager, "player", "music.artists");

        assertEquals("player", artists.getObjectID());
        assertEquals("music.artists", artists.getPath());
        assertEquals("player.music.artists", artists.getLocation());
    }
}
