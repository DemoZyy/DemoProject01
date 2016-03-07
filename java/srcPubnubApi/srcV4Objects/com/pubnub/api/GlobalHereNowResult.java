package com.pubnub.api;

public class GlobalHereNowResult extends Result {
    GlobalHereNowData data;

    public GlobalHereNowResult() {
        data = new GlobalHereNowData();
    }
    
    public GlobalHereNowData getData() {
        return data;
    }

    void setData(GlobalHereNowData data) {
        this.data = data;
    }

    public String toString() {
        String s = super.toString();
        s = s + data + "\n";
        return s;
        
    }
}
