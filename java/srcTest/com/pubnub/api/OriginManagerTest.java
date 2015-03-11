package com.pubnub.api;

import org.junit.Before;
import org.junit.Test;

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
        pubnub.setOriginHeartbeatInterval(originHeartbeatInterval);
        pubnub.setNonSubscribeTimeout(921);
        pubnub.setOriginHeartbeatIntervalAfterFailure(299);
        pubnub.setOriginHeartbeatMaxRetries(originHeartbeatMaxRetries);
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

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
            }
        });

        assertTrue(pubnub.isOriginManagerRunning());
        int timeout = (originHeartbeatInterval + 1) * (originHeartbeatMaxRetries + 2) * 1000;

        Thread.sleep(timeout);

        assertEquals(4, pubnub.getOriginsPool().size());
        assertFalse(pubnub.getOriginsPool().contains("ps5"));

        pubnub.unsubscribe("demo");
        assertFalse(pubnub.isOriginManagerRunning());

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
            }
        });

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

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
            }
        });

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
}
