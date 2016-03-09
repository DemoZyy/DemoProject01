package com.pubnub.api;

public interface PubnubCGAsyncInterface {
    PubnubCGAsyncAddChannelInterface  addChannel();
    PubnubCGAsyncRemoveChannelInterface  removeChannel();
    PubnubCGAsyncRemoveGroupInterface  removeGroup();
    PubnubCGAsyncListChannelsInterface listChannels();
    PubnubCGAsyncListGroupsInterface  listGroups();
}
