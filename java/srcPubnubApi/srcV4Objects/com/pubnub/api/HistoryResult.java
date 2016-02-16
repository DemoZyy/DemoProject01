package com.pubnub.api;

public class HistoryResult extends Result {
    HistoryData data;

    HistoryResult(){
        this.type = ResultType.RESULT;
        this.data = new HistoryData();
    }
    
    public HistoryData getData() {
        return data;
    }
    
    public String toString() {
        String s = super.toString();
        s = s + data + "\n";
        return s;
    }
    
}
