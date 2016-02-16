package com.pubnub.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HereNowData {
	int occupancy;
	public int getOccupancy() {
        return occupancy;
    }

    public int getTotalOccupancy() {
        return totalOccupancy;
    }

    public HereNowUuidData[] getUuids() {
        return uuids;
    }

    int totalOccupancy;
	HereNowUuidData[] uuids;
	
	
	public String toString() {

		String s = "";
		s = s + "Occupancy: " + occupancy + "\n";
		s = s + "Total Occupancy: " + occupancy + "\n";
		if (uuids != null) s = s + "UUIDS: " + HereNowUuidData.arrayToString(uuids) + "\n";

		return s;
	}

    public static HereNowUuidData[] getUuidDataArray(JSONArray jsonArray) {
        System.out.println(jsonArray.toString());
        HereNowUuidData[] uuidData = new HereNowUuidData[jsonArray.length()];
        
        for (int i = 0; i < jsonArray.length(); i++) {
            Object a;
            try {
                a = jsonArray.get(i);
                HereNowUuidData hd = uuidData[i] = new HereNowUuidData();
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
        System.out.println(HereNowUuidData.arrayToString(uuidData));
        return uuidData;
    }
}
