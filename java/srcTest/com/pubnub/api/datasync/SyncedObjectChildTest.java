package com.pubnub.api.datasync;

import com.pubnub.api.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SyncedObjectChildTest {
    Pubnub pubnub;
    SyncedObjectManager syncedObjectManager;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");
        syncedObjectManager = new SyncedObjectManager(pubnub);

        pubnub.setCacheBusting(false);
    }

    @Test
    public void testRootLevelSyncedObjectInstantiation() {
        SyncedObject player = new SyncedObject(syncedObjectManager, "player");

        assertEquals("player", player.getObjectID());
        assertEquals("", player.getPath());
        assertEquals("player", player.getLocation());
    }

    @Test
    public void testInnerLevelSyncedObjectInstantiation() {
        SyncedObject artists = new SyncedObject(syncedObjectManager, "player", "music.artists");

        assertEquals("player", artists.getObjectID());
        assertEquals("music.artists", artists.getPath());
        assertEquals("player.music.artists", artists.getLocation());
    }

    @Test
    public void testGetFirstLevelChild() {
        SyncedObject player = pubnub.sync("player");
        SyncedObject music = player.child("music");

        assertEquals("player", music.getObjectID());
        assertEquals("music", music.getPath());
        assertEquals("player.music", music.getLocation());
    }

    @Test
    public void testGetSecondLevelChild() {
        SyncedObject player = pubnub.sync("player");
        SyncedObject artists = player.child("music.artists");

        assertEquals("player", artists.getObjectID());
        assertEquals("music.artists", artists.getPath());
        assertEquals("player.music.artists", artists.getLocation());
    }
}
