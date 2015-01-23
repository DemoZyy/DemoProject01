package com.pubnub.api.datasync;

import com.pubnub.api.JSONHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONHelperTest {
    @Test
    public void testStaticMerge1stLevel() throws JSONException {
        JSONObject player = DataSyncTestHelper.getPlayer();
        JSONObject settings = new JSONObject("{\"settings\":{\"volume\":25}}");

        JSONHelper.merge(player, settings);

        assertEquals(3, player.getJSONObject("settings").length());
        assertEquals(25, player.getJSONObject("settings").getInt("volume"));
    }

    @Test
    public void testStaticMerge2ndLevel() throws JSONException {
        JSONObject home1 = new JSONObject();
        JSONObject home2 = new JSONObject("{\"player\": {\"settings\":{\"volume\":25}}}");
        JSONObject player = DataSyncTestHelper.getPlayer();

        player.put("statistics", new JSONObject("{\"current\": \"track#1\"}"));
        home1.put("player", player);

        JSONHelper.merge(home1, home2);

        assertEquals(3, home1.getJSONObject("player").getJSONObject("settings").length());
        assertEquals(25, home1.getJSONObject("player").getJSONObject("settings").getInt("volume"));
        assertEquals("track#1", home1.getJSONObject("player").getJSONObject("statistics").getString("current"));
    }

    @Test
    public void testStaticMerge2ndLevelWithLocation() throws JSONException {
        JSONObject home1 = new JSONObject();
        JSONObject home2 = new JSONObject("{\"settings\":{\"volume\":25}}");
        JSONObject player = DataSyncTestHelper.getPlayer();

        player.put("statistics", new JSONObject("{\"current\": \"track#1\"}"));
        home1.put("player", player);

        JSONHelper.merge(home1, home2, "player");

        assertEquals(3, home1.getJSONObject("player").getJSONObject("settings").length());
        assertEquals(25, home1.getJSONObject("player").getJSONObject("settings").getInt("volume"));
        assertEquals("track#1", home1.getJSONObject("player").getJSONObject("statistics").getString("current"));
    }
}
