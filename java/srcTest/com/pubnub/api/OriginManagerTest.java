package com.pubnub.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class OriginManagerTest {
    Pubnub pubnub;
    int originHeartbeatInterval = 2;
    int originHeartbeatMaxRetries = 2;

    @Before
    public void setUp() {
        pubnub = new Pubnub("demo", "demo");
        pubnub.setOriginManagerInterval(originHeartbeatInterval);
        pubnub.setNonSubscribeTimeout(921);
        pubnub.setOriginManagerIntervalAfterFailure(299);
        pubnub.setOriginManagerMaxRetries(originHeartbeatMaxRetries);
    }

    @Test
    public void testExplicitlyEnabledSetOriginBeforeSubscribe() throws PubnubException, InterruptedException {
        LinkedHashSet<String> originsPool = new LinkedHashSet<String>();

        originsPool.add("geo5.devbuild");
        originsPool.add("geo1.devbuild");
        originsPool.add("geo2.devbuild");
        originsPool.add("geo3.devbuild");
        originsPool.add("geo4.devbuild");

        pubnub.setOriginsPool(originsPool, true);

        pubnub.subscribe("demo", new Callback() {});

        assertTrue(pubnub.isOriginManagerRunning());

        int timeout = (originHeartbeatInterval + 1) * (originHeartbeatMaxRetries + 2) * 1000;

        Thread.sleep(timeout);

        assertEquals(4, pubnub.getOriginsPool().size());
        assertFalse(pubnub.getOriginsPool().contains("ps5"));

        pubnub.unsubscribe("demo");
        assertFalse(pubnub.isOriginManagerRunning());

        pubnub.subscribe("demo", new Callback() {});

        assertTrue(pubnub.isOriginManagerRunning());
        assertEquals(4, pubnub.getOriginsPool().size());

        pubnub.disableOriginManager();

        assertFalse(pubnub.isOriginManagerRunning());
    }

    @Test
    public void testExplicitlyEnabledSetOriginAfterSubscribe() throws PubnubException, InterruptedException {
        LinkedHashSet<String> originsPool = new LinkedHashSet<String>();

        originsPool.add("geo5.devbuild");
        originsPool.add("geo1.devbuild");
        originsPool.add("geo2.devbuild");
        originsPool.add("geo3.devbuild");
        originsPool.add("geo4.devbuild");

        pubnub.setOriginsPool(originsPool);
        pubnub.subscribe("demo", new Callback() {});

        assertFalse(pubnub.isOriginManagerRunning());

        pubnub.enableOriginManager();

        assertTrue(pubnub.isOriginManagerRunning());

        int timeout = (originHeartbeatInterval + 1) * (originHeartbeatMaxRetries + 1) * 1000;

        Thread.sleep(timeout);

        assertEquals(4, pubnub.getOriginsPool().size());
        assertFalse(pubnub.getOriginsPool().contains("ps5"));

        pubnub.unsubscribe("demo");

        assertFalse(pubnub.isOriginManagerRunning());
    }

    @Test
    public void testSetOriginsPoolViaStringArray() throws PubnubException {
        pubnub.setOriginsPool(new String[]{"ps1", "ps2"});

        OriginsPool originsPool = pubnub.getOriginsPool();

        assertEquals(2, originsPool.size());
        assertTrue(originsPool.contains("ps1"));
        assertTrue(originsPool.contains("ps2"));

        assertFalse((Boolean) Whitebox.getInternalState(pubnub, "originManagerExplicitlyEnabled"));
    }

    @Test
    public void testSetOriginsPoolViaStringArrayAndEnable() throws PubnubException {
        pubnub.setOriginsPool(new String[]{"ps1", "ps2"}, true);

        OriginsPool originsPool = pubnub.getOriginsPool();

        assertEquals(2, originsPool.size());
        assertTrue(originsPool.contains("ps1"));
        assertTrue(originsPool.contains("ps2"));

        assertTrue((Boolean) Whitebox.getInternalState(pubnub, "originManagerExplicitlyEnabled"));
    }

    @Test
    public void testSetOriginsPoolViaList() throws PubnubException {
        LinkedHashSet<String> originsList = new LinkedHashSet<String>();
        originsList.add("ps1");
        originsList.add("ps2");

        pubnub.setOriginsPool(originsList);

        OriginsPool originsPool = pubnub.getOriginsPool();

        assertEquals(2, originsPool.size());
        assertTrue(originsPool.contains("ps1"));
        assertTrue(originsPool.contains("ps2"));
    }
}
