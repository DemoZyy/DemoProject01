package com.pubnub.api;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class URLGeneratorTest {
    Pubnub pubnub;

    @Before
    public void setUp() {
        pubnub = new Pubnub("demo", "demo");
    }

    @Test
    public void testGetURL() {
        assertEquals("http://pubsub-1.pubnub.com", pubnub.getPubnubUrl());
    }

    @Test
    public void testGetURLWithCacheBustingDisabled() {
        pubnub.setCacheBusting(false);
        assertEquals("http://pubsub.pubnub.com", pubnub.getPubnubUrl());
    }

    @Test
    public void testGetUrlWithAndWithoutOriginManager() throws PubnubException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        pubnub.setOriginsPool(new String[]{"origin1", "origin2"}, true);

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void connectCallback(String channel, Object message) {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        assertTrue(pubnub.isOriginManagerRunning());
        assertEquals("http://origin1.pubnub.com", pubnub.getPubnubUrl());

        pubnub.unsubscribe("demo");

        assertFalse(pubnub.isOriginManagerRunning());
        assertTrue(pubnub.getPubnubUrl().matches("^http://pubsub-\\d+\\.pubnub\\.com$"));
    }
}
