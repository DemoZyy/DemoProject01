package com.pubnub.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OriginManager.class)
public class OriginManagerBehaviourTest {
    Pubnub pubnub;
    int originHeartbeatInterval = 2;
    int originHeartbeatMaxRetries = 2;

    @Before
    public void setUp() {
        pubnub = PowerMockito.spy(new Pubnub("demo", "demo"));
        pubnub.setOriginManagerInterval(originHeartbeatInterval);
        pubnub.setNonSubscribeTimeout(921);
        pubnub.setOriginManagerIntervalAfterFailure(299);
        pubnub.setOriginManagerMaxRetries(originHeartbeatMaxRetries);
    }

    @Test
    public void testStoryOne() throws Exception {
        pubnub.setOriginsPool(new String[]{"ps1", "ps2", "ps3"});

        final CountDownLatch latch = new CountDownLatch(1);

        final OriginManager originManager = PowerMockito.spy(new OriginManager(pubnub));

        Answer answer = new Answer<Object>() {
            private AtomicInteger psOneCounter = new AtomicInteger(0);

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback cb = (Callback) args[1];

                if (cb != null) {
                    if (psOneCounter.getAndIncrement() < 5) {
                        cb.successCallback("hey", new Object());
                    } else {
                        psOneCounter.set(0);
                        cb.errorCallback("hey", PubnubError.PNERROBJ_PUBNUB_ERROR);
                    }
                }

                return null;
            }
        };

        PowerMockito.doAnswer(answer).when(originManager, "isOriginOnline", anyString(), any(Callback.class));
        PowerMockito.doReturn(originManager).when(pubnub).getOriginManager();

        pubnub.enableOriginManager();

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void connectCallback(String channel, Object message) {
                latch.countDown();
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                latch.countDown();
            }
        });

        latch.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());
    }

    @Test
    public void testShouldInvokeErrorCallbackIfOriginsPoolIsNotSetYetWhileNotSubscribed() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        pubnub.enableOriginManager(new Callback() {
            @Override
            public void errorCallback(String channel, PubnubError error) {
                latch.countDown();
            }
        });

        pubnub.subscribe("demo", new Callback() {});

        latch.await(3, TimeUnit.SECONDS);

        assertEquals(0, latch.getCount());

        Mockito.verify(pubnub, never()).disconnectAndResubscribe();
    }

    @Test
    public void testShouldInvokeErrorCallbackIfOriginsPoolIsNotSetYetWhileSubscribed() throws Exception {
        final CountDownLatch subscribeLatch = new CountDownLatch(1);
        final CountDownLatch originManagerLatch = new CountDownLatch(1);

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void connectCallback(String channel, Object message) {
                subscribeLatch.countDown();
            }
        });

        subscribeLatch.await(3, TimeUnit.SECONDS);

        pubnub.enableOriginManager(new Callback() {
            @Override
            public void errorCallback(String channel, PubnubError error) {
                originManagerLatch.countDown();
            }
        });

        originManagerLatch.await(3, TimeUnit.SECONDS);

        assertEquals(0, subscribeLatch.getCount());
        assertEquals(0, originManagerLatch.getCount());

        Mockito.verify(pubnub, never()).disconnectAndResubscribe();
    }

    @Test
    public void testShouldNotInvokeErrorCallbackIfOriginsPoolIsSetWhileNotSubscribed() throws Exception {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        pubnub.enableOriginManager(new Callback() {
            @Override
            public void errorCallback(String channel, PubnubError error) {
                latch1.countDown();
            }
        });

        pubnub.setOriginsPool(new String[]{"ps1", "ps2"});

        pubnub.publish("demo", "hey", new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                latch2.countDown();
            }
        });

        Mockito.verify(pubnub, never()).disconnectAndResubscribe();

        latch2.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch2.getCount());
        latch1.await(1, TimeUnit.SECONDS);
        assertEquals(1, latch1.getCount());
    }

    @Test
    public void testShouldStopOriginManagerOnUnsubscribe() throws Exception {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        pubnub.enableOriginManager(new Callback() {
            @Override
            public void errorCallback(String channel, PubnubError error) {
                latch1.countDown();
            }
        });

        pubnub.setOriginsPool(new String[]{"ps1", "ps2"});

        pubnub.subscribe("demo", new Callback() {
            @Override
            public void connectCallback(String channel, Object message) {
                latch2.countDown();
            }
        });

        latch2.await(3, TimeUnit.SECONDS);
        assertEquals(0, latch2.getCount());
        assertTrue(pubnub.getOriginManager().isCurrentOriginManagerRunning());

        pubnub.unsubscribe("demo");

        assertFalse(pubnub.getOriginManager().isCurrentOriginManagerRunning());
        latch1.await(1, TimeUnit.SECONDS);
        assertEquals(1, latch1.getCount());
    }
}
