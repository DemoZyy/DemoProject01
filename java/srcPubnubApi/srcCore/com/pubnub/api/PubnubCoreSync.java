package com.pubnub.api;

import java.io.IOException;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

abstract class PubnubCoreSync extends PubnubCore {

    public PubnubCoreSync(){
        
    }
    
    private HttpClient httpClient;

    protected HttpResponse fetch(String url) throws IOException, PubnubException {
        if (httpClient == null)
            return null;
        return httpClient.fetch(url);
    }

    protected void init() {
        // sync client

        Hashtable headers = new Hashtable();
        headers.put("V", VERSION);
        headers.put("Accept-Encoding", "gzip");
        headers.put("User-Agent", getUserAgent());
        CACHE_BUSTING = false;
        httpClient = HttpClient.getClient(5000, 5000, headers);

    }


    public JSONArray time() {
        return _time(null, true);
    }

}
