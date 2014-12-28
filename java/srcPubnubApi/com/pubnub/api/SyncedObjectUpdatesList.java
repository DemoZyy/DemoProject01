package com.pubnub.api;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncedObjectUpdatesList extends ArrayList {
    AtomicBoolean complete;

    SyncedObjectUpdatesList () {
        complete = new AtomicBoolean();
    }

    public Boolean isComplete() {
        return complete.get();
    }

    public void setComplete(Boolean status) {
        complete.set(status);
    }
}
