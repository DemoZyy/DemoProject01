package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.TestHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class DataSyncPAMTest {
    Pubnub pubnub;
    String objectName;
    String auth_key;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36", "demo-36");

        pubnub.setCacheBusting(false);

        objectName = "syncedObjectTest-" + TestHelper.random();
        auth_key = "user-ak-" + TestHelper.random();
    }

    @Test
    public void testGrantObjectToAll() throws InterruptedException, JSONException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        final CountDownLatch latch4 = new CountDownLatch(1);

        TestHelper.SimpleCallback cb1 = new TestHelper.SimpleCallback(latch1);
        TestHelper.SimpleCallback cb2 = new TestHelper.SimpleCallback(latch2);
        TestHelper.SimpleCallback cb3 = new TestHelper.SimpleCallback(latch3);
        TestHelper.SimpleCallback cb4 = new TestHelper.SimpleCallback(latch4);

        pubnub.pamRevokeSyncedObject(objectName, cb1);
        latch1.await(10, TimeUnit.SECONDS);

        pubnub.pamAuditSyncedObject(objectName, cb2);
        latch2.await(10, TimeUnit.SECONDS);

        JSONObject permissions = ((JSONObject) cb2.getResponse());
        JSONObject auths = permissions
                .getJSONObject("objects")
                .getJSONObject(objectName);

        assertEquals("datasync", permissions.getString("level"));
        assertEquals(0, auths.getInt("w"));
        assertEquals(0, auths.getInt("r"));
        assertEquals(0, auths.getInt("m"));

        pubnub.pamGrantSyncedObject(objectName, true, true, cb3);
        latch3.await(10, TimeUnit.SECONDS);

        Thread.sleep(5000);
        pubnub.pamAuditSyncedObject(objectName, cb4);
        latch4.await(10, TimeUnit.SECONDS);

        permissions = ((JSONObject) cb4.getResponse());
        auths = permissions
                .getJSONObject("objects")
                .getJSONObject(objectName);

        assertEquals("datasync", permissions.getString("level"));
        assertEquals(1, auths.getInt("w"));
        assertEquals(1, auths.getInt("r"));
        assertEquals(0, auths.getInt("m"));
    }

    @Test
    public void testGrantSyncedObjectToUser()
            throws InterruptedException, PubnubException, JSONException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        final CountDownLatch latch4 = new CountDownLatch(1);

        TestHelper.SimpleCallback cb1 = new TestHelper.SimpleCallback(latch1) {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
            }
        };
        TestHelper.SimpleCallback cb2 = new TestHelper.SimpleCallback(latch2);
        TestHelper.SimpleCallback cb3 = new TestHelper.SimpleCallback(latch3);
        TestHelper.SimpleCallback cb4 = new TestHelper.SimpleCallback(latch4);

        pubnub.pamRevokeSyncedObject(objectName, auth_key, cb1);
        latch1.await(10, TimeUnit.SECONDS);

        pubnub.pamAuditSyncedObject(objectName, auth_key, cb2);
        latch2.await(10, TimeUnit.SECONDS);

        JSONObject permissions = ((JSONObject) cb2.getResponse());
        JSONObject auths = permissions.getJSONObject("auths").getJSONObject(auth_key);

        assertEquals("datasync+user", permissions.getString("level"));
        assertEquals(objectName, permissions.getString("object"));
        assertEquals(0, auths.getInt("w"));
        assertEquals(0, auths.getInt("r"));
        assertEquals(0, auths.getInt("m"));

        pubnub.pamGrantSyncedObject(objectName, auth_key, true, true, cb3);
        latch3.await(10, TimeUnit.SECONDS);

        Thread.sleep(5000);
        pubnub.pamAuditSyncedObject(objectName, auth_key, cb4);
        latch4.await(10, TimeUnit.SECONDS);

        permissions = ((JSONObject) cb4.getResponse());
        auths = permissions.getJSONObject("auths").getJSONObject(auth_key);

        assertEquals("datasync+user", permissions.getString("level"));
        assertEquals(objectName, permissions.getString("object"));
        assertEquals(1, auths.getInt("w"));
        assertEquals(1, auths.getInt("r"));
        assertEquals(0, auths.getInt("m"));
    }
}
