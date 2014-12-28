package com.pubnub.api;

import java.util.List;

public class DataSyncCallback {

    public void readyCallback() {
    }

    public void readyCallback(SyncedObject syncedObject) {
    }

    public void mergeCallback(List updates, String path) {
    }

    public void replaceCallback(List updates, String path) {
    }

    public void removeCallback(List updates, String path) {
    }

    public void errorCallback(PubnubError error) {
    }

    public void connectCallback(String channel, Object message) {
    }

    public void reconnectCallback(String channel, Object message) {
    }

    public void disconnectCallback(String channel, Object message) {
    }

    public void invokeReadyCallback(SyncedObject syncedObject) {
        readyCallback();
        readyCallback(syncedObject);
    }
}
