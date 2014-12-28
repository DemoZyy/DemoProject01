package com.pubnub.api.datasync;

import com.pubnub.api.Pubnub;
import com.pubnub.api.SyncedObject;
import com.pubnub.api.SyncedObjectManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SyncedObjectStaticHelpersTest {
    Pubnub pubnub;

    JSONObject rawChildren;
    JSONObject rawPlayer;
    JSONObject expectedChildren;
    JSONObject expectedPlayer;

    @Before
    public void setUp() throws InterruptedException, JSONException {
        pubnub = new Pubnub("demo", "demo");
        pubnub.setCacheBusting(false);

        rawPlayer = new JSONObject("{\"music\": {\"artists\": {\"-!14172480320903800\": {\"last\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Sinatra\"}, \"first\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Frank\"}}, \"-!14172480321030890\": {\"last\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Dylan\"}, \"first\": {\"pn_tt\": \"14172480320902200\", \"pn_val\": \"Bob\"}}}}, 'i':123, 'b':true, 's': 'abc'}");
        rawChildren = new JSONObject("{\"-a!14189386794825700\": {\"pn_tt\": \"14189386794826050\", \"pn_val\": \"alice\"}, \"-z!14189386904427630\": {\"pn_tt\": \"14189386904428010\", \"pn_val\": \"bob\"}, \"-n!14189387080996980\": {\"pn_tt\": \"14189387080997350\", \"pn_val\": \"james\"}}");

        expectedPlayer = new JSONObject("{\"music\":{\"artists\":[{\"last\":\"Sinatra\",\"first\":\"Frank\"},{\"last\":\"Dylan\",\"first\":\"Bob\"}]}}");
        expectedChildren = new JSONObject("{\"children\": [\"alice\",\"bob\",\"james\"]}");
    }

    @Test
    public void testMergeJson() throws JSONException {
        JSONObject result = new JSONObject("{'music': {'artists':{}}, 'list':['a','b','c']}");

        SyncedObjectManager.merge(result, new JSONObject("{'artists': 12345}"), "music");
        assertEquals(result.getJSONObject("music").getInt("artists"), 12345);

        SyncedObjectManager.updateJSONObjectValue(result, null, "list");
        assertFalse(result.has("list"));

        SyncedObjectManager.updateJSONObjectValue(result, 55, "a.b.c");
        assertEquals(result.getJSONObject("a").getJSONObject("b").getInt("c"), 55);
    }

    @Test
    public void testIsPnList() throws JSONException {
        assertTrue(SyncedObject.isPnList(rawChildren));
        assertTrue(SyncedObject.isPnList(rawPlayer.getJSONObject("music").getJSONObject("artists")));
        assertFalse(SyncedObject.isPnList(rawPlayer));
    }

    @Test
    public void testGlue() {
        assertEquals("player", SyncedObject.glue("player", ""));
        assertEquals("player.music", SyncedObject.glue("player", "music"));
        assertEquals("music", SyncedObject.glue("", "music"));
    }
}
