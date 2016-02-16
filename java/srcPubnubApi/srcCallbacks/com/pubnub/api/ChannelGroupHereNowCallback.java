package com.pubnub.api;

public abstract class ChannelGroupHereNowCallback extends Callback {
    public abstract void status(ErrorStatus status);
    public abstract void result(ChannelGroupHereNowResult result);
}
