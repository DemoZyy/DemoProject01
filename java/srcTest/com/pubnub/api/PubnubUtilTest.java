package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PubnubUtilTest {

    @Test
    public void testIsBlank() {
        assertTrue(PubnubUtil.isBlank(""));
        assertTrue(PubnubUtil.isBlank(null));
        assertTrue(PubnubUtil.isPresent("string"));
        try {
            assertTrue(PubnubUtil.isBlank(new JSONObject("{}")));
            assertTrue(PubnubUtil.isBlank(new JSONArray("[]")));
        } catch (JSONException e) {
            fail();
        }
    }
}
