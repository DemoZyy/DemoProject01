package com.pubnub.api;

public class WhereNowResult extends Result {
    WhereNowData data;

    public WhereNowResult() {
        data = new WhereNowData();
    }
    
    public WhereNowData getData() {
        return data;
    }

    void setData(WhereNowData data) {
        this.data = data;
    }

    public String toString() {
        String s = super.toString();
        s = s + data + "\n";
        return s;
        
    }
}
