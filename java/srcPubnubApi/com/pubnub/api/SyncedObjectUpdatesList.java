package com.pubnub.api;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncedObjectUpdatesList extends ArrayList {
    AtomicBoolean complete;

    SyncedObjectUpdatesList () {
        complete = new AtomicBoolean();
    }

    public boolean isComplete() {
        return complete.get();
    }

    public void setComplete(boolean status) {
        complete.set(status);
    }
}
