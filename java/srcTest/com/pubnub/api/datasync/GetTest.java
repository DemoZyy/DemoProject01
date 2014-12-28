package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.TestHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class GetTest {
    Pubnub pubnub;

    @Before
    public void setUp() throws InterruptedException {
        pubnub = new Pubnub("demo-36", "demo-36");

        pubnub.setCacheBusting(false);
    }

    @Test
    public void testGetResponse() throws InterruptedException, JSONException {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);

        TestHelper.SimpleCallback cb1 = new TestHelper.SimpleCallback(latch1);
        TestHelper.SimpleCallback cb2 = new TestHelper.SimpleCallback(latch2);

        Hashtable<String, Object> mergeArgs = new Hashtable<String, Object>();

        mergeArgs.put("location", "player.settings.volume");
        mergeArgs.put("data", 70);

        pubnub.merge(mergeArgs, cb1);
        latch1.await(5, TimeUnit.SECONDS);

        Hashtable<String, Object> getArgs = new Hashtable<String, Object>();

        getArgs.put("location", "player");

        pubnub.get(getArgs, cb2);
        latch2.await(5, TimeUnit.SECONDS);

        JSONObject response = (JSONObject) cb2.getResponse();
        Number volume = response.getJSONObject("data")
                .getJSONObject("settings")
                .getJSONObject("volume")
                .getInt("pn_val");

        assertEquals("", response.getString("location"));
        assertEquals("get", response.getString("op"));
        assertEquals(70, volume);
        assertEquals(0, latch2.getCount());
    }
}
