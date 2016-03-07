package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GlobalHereNowData {
    int occupancy;
    public int getOccupancy() {
        return occupancy;
    }

    public int getTotalOccupancy() {
        return totalOccupancy;
    }

    public GlobalHereNowUuidData[] getUuids() {
        return uuids;
    }

    int totalOccupancy;
    GlobalHereNowUuidData[] uuids;
    
    
    public String toString() {

        String s = "";
        s = s + "Occupancy: " + occupancy + "\n";
        s = s + "Total Occupancy: " + occupancy + "\n";
        if (uuids != null) s = s + "UUIDS: " + GlobalHereNowUuidData.arrayToString(uuids) + "\n";

        return s;
    }

    public static GlobalHereNowUuidData[] getUuidDataArray(JSONArray jsonArray) {
        System.out.println(jsonArray.toString());
        GlobalHereNowUuidData[] uuidData = new GlobalHereNowUuidData[jsonArray.length()];
        
        for (int i = 0; i < jsonArray.length(); i++) {
            Object a;
            try {
                a = jsonArray.get(i);
                GlobalHereNowUuidData hd = uuidData[i] = new GlobalHereNowUuidData();
                if (a instanceof JSONObject) {
                    JSONObject jso = (JSONObject)a;
                    hd.uuid = jso.getString("uuid");
                    hd.metadata = jso.getJSONObject("metadata");
                } else if (a instanceof String) {
                    hd.uuid = (String) a;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                //System.out.println(e);
                //e.printStackTrace();
            }

        }

        return uuidData;
    }
}
