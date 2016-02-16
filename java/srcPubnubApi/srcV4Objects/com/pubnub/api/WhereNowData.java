package com.pubnub.api;

public class WhereNowData {
    String[] channels;
    public String[] getChannels() {
        return channels;
    }

    public String toString() {

        String s = "";
        s = s + "Channels: " + PubnubUtil.joinString(channels, ", ") + "\n";

        return s;
    }
}
