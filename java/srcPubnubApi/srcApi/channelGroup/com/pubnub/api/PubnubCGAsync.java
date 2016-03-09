package com.pubnub.api;

public class PubnubCGAsync implements PubnubCGAsyncInterface{

    final Pubnub pubnub;
    
    ChannelGroupChangeCallback channelGroupChangeCallback;
    GroupAuditCallback groupAuditCallback;
    GroupChannelsAuditCallback  groupChannelsAuditCallback;
    
    String channel;
    String[] channels;
    String channelGroup;
    
    PubnubCGAsync pns = this;
    
    
    PubnubCGAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    
    
    @Override
    public PubnubCGAsyncAddChannelInterface addChannel() {
        
        
        PubnubCGAsyncAddChannelEnd apiEnd = new PubnubCGAsyncAddChannelEnd(){

            @Override
            public void add() {
                if (pns.channel != null) {
                    pubnub.channelGroupAddChannel(
                            pns.channelGroup, pns.channel, pns.channelGroupChangeCallback);
                } else if (pns.channels != null) {
                    pubnub.channelGroupAddChannel(
                            pns.channelGroup, pns.channels, pns.channelGroupChangeCallback);
                }
            }
            
        };
        
        PubnubCGAsyncAddChannelApiStateChannel apiChannel = 
                new PubnubCGAsyncAddChannelApiStateChannel() {

                    @Override
                    public PubnubCGAsyncAddChannelEnd channel(String channel) {
                        pns.channel = channel;
                        return apiEnd;
                    }

                    @Override
                    public PubnubCGAsyncAddChannelEnd channels(String[] channels) {
                        pns.channels = channels;
                        return apiEnd;
                    }
            
        };
        
        
        PubnubCGAsyncAddChannelApiStateChannelGroup apiChannelGroup = 
                new PubnubCGAsyncAddChannelApiStateChannelGroup(){

                    @Override
                    public PubnubCGAsyncAddChannelApiStateChannel channelGroup(String channelGroup) {
                        pns.channelGroup = channelGroup;
                        return apiChannel;
                    }
            
        };
        
        PubnubCGAsyncAddChannelInterface apiAddChannel = new PubnubCGAsyncAddChannelInterface(){

            @Override
            public PubnubCGAsyncAddChannelApiStateChannelGroup callback(ChannelGroupChangeCallback callback) {
                pns.channelGroupChangeCallback = callback;
                return apiChannelGroup;
            }
            
        };
        return apiAddChannel;
    }
    
    
    @Override
    public PubnubCGAsyncRemoveChannelInterface removeChannel() {
        
        PubnubCGAsyncRemoveChannelEnd apiEnd = new PubnubCGAsyncRemoveChannelEnd() {

            @Override
            public void remove() {
                if (pns.channel != null) {
                    pubnub.channelGroupRemoveChannel(channelGroup, channel, pns.channelGroupChangeCallback);
                } else if (pns.channels != null) {
                    pubnub.channelGroupRemoveChannel(channelGroup, channels, pns.channelGroupChangeCallback);
                }

            }
            
        };
        
        PubnubCGAsyncRemoveChannelApiStateChannel apiChannel = 
                new PubnubCGAsyncRemoveChannelApiStateChannel() {

                    @Override
                    public PubnubCGAsyncRemoveChannelEnd channel(String channel) {
                        pns.channel = channel;
                        return apiEnd;
                    }

                    @Override
                    public PubnubCGAsyncRemoveChannelEnd channels(String[] channels) {
                        pns.channels = channels;
                        return apiEnd;
                    }
            
        };
        
        PubnubCGAsyncRemoveChannelApiStateChannelGroup apiChannelGroup = 
                new PubnubCGAsyncRemoveChannelApiStateChannelGroup() {

                    @Override
                    public PubnubCGAsyncRemoveChannelApiStateChannel channelGroup(String channelGroup) {
                        pns.channelGroup = channelGroup;
                        return apiChannel;
                    }
            
        };
        
        PubnubCGAsyncRemoveChannelInterface apiRemoveChannel = new PubnubCGAsyncRemoveChannelInterface(){

            @Override
            public PubnubCGAsyncRemoveChannelApiStateChannelGroup callback(ChannelGroupChangeCallback callback) {
                pns.channelGroupChangeCallback = callback;
                return apiChannelGroup;
            }
            
        };
        return apiRemoveChannel;
    }
    
    
    @Override
    public PubnubCGAsyncRemoveGroupInterface removeGroup() {
        
        PubnubCGAsyncRemoveGroupEnd apiEnd = new PubnubCGAsyncRemoveGroupEnd() {

            @Override
            public void remove() {
                if (pns.channel != null) {
                    pubnub.channelGroupRemoveChannel(
                            pns.channelGroup, pns.channel, pns.channelGroupChangeCallback);
                } else if (pns.channels != null) {
                    pubnub.channelGroupRemoveChannel(
                            pns.channelGroup, pns.channels, pns.channelGroupChangeCallback);   
                }
            }
            
        };
        
        PubnubCGAsyncRemoveGroupApiStateChannelGroup apiChannelGroup =
                new PubnubCGAsyncRemoveGroupApiStateChannelGroup() {

                    @Override
                    public PubnubCGAsyncRemoveGroupEnd channelGroup(String channelGroup) {
                        pns.channelGroup = channelGroup;
                        return apiEnd;
                    }
            
        };
        
        PubnubCGAsyncRemoveGroupInterface apiRemoveGroup =
                new PubnubCGAsyncRemoveGroupInterface(){

                    @Override
                    public PubnubCGAsyncRemoveGroupApiStateChannelGroup callback(ChannelGroupChangeCallback callback) {
                        pns.channelGroupChangeCallback = callback;
                        return apiChannelGroup;
                    }
            
        };
        return apiRemoveGroup;
    }
    
    
    @Override
    public PubnubCGAsyncListChannelsInterface listChannels() {
        
        PubnubCGAsyncListChannelsEnd apiEnd = new PubnubCGAsyncListChannelsEnd() {

            @Override
            public void list() {
                pubnub.channelGroupListChannels(pns.channelGroup, pns.groupChannelsAuditCallback);
            }
            
        };
        
        PubnubCGAsyncListChannelsApiStateChannelGroup apiChannelGroup = 
                new PubnubCGAsyncListChannelsApiStateChannelGroup() {

                    @Override
                    public PubnubCGAsyncListChannelsEnd channelGroup(String channelGroup) {
                        pns.channelGroup = channelGroup;
                        return apiEnd;
                    }
            
        };
        
        PubnubCGAsyncListChannelsInterface apiListChannels = 
                new PubnubCGAsyncListChannelsInterface() {

                    @Override
                    public PubnubCGAsyncListChannelsApiStateChannelGroup callback(
                            GroupChannelsAuditCallback callback) {
                        pns.groupChannelsAuditCallback = callback;
                        return null;
                    }
            
        };
        return apiListChannels;
    }
    
    PubnubCGAsyncListGroupsEnd apiListGroupsEnd = 
            new PubnubCGAsyncListGroupsEnd() {

                @Override
                public void list() {
                    pubnub.channelGroupListGroups(pns.groupAuditCallback);   
                }
        
    };
    
    PubnubCGAsyncListGroupsInterface apiListGroups = 
            new PubnubCGAsyncListGroupsInterface() {

                @Override
                public PubnubCGAsyncListGroupsEnd callback(GroupAuditCallback callback) {
                    pns.groupAuditCallback = callback;
                    return apiListGroupsEnd;
                }
        
    };
    
    
    @Override
    public PubnubCGAsyncListGroupsInterface listGroups() {
        return apiListGroups;
    }
}
