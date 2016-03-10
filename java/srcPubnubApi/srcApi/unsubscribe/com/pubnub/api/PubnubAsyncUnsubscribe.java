package com.pubnub.api;

public class PubnubAsyncUnsubscribe implements PubnubUnsubscribeAsyncInterface {


    final Pubnub pubnub;
    
    UnsubscribeCallback callback;

    String channel;
    String channelGroup;
    
    String[] channels;
    String[] channelGroups;
    
    boolean presence;
    boolean allChannelGroups;
    boolean allChannels;

    
    PubnubAsyncUnsubscribe pns = this;
    
    
    PubnubAsyncUnsubscribe(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    PubnubUnsubscribeAsyncPresence apiPresence = new PubnubUnsubscribeAsyncPresence() {

        PubnubAsyncUnsubscribeEnd apiEnd = new PubnubAsyncUnsubscribeEnd() {

            @Override
            public void invoke() {
                if (allChannels) {
                    pubnub.unsubscribeAll(callback);
                }
                else if (allChannelGroups) {
                    pubnub.channelGroupUnsubscribeAllGroups(callback);
                }
                else if (channel != null) {
                    pubnub.unsubscribe(new String[]{channel}, callback);
                }
                else if (channelGroup != null) {
                    pubnub.channelGroupUnsubscribe(new String[]{channelGroup}, callback);
                }
                else if (channels != null) {
                    pubnub.unsubscribe(channels, callback);
                }
                else if (channelGroups != null) {
                    pubnub.channelGroupUnsubscribe(channelGroups, callback);
                }
            }
            
        };
        
        PubnubUnsubscribeAsyncChannel apiChannel = new PubnubUnsubscribeAsyncChannel() {

            @Override
            public PubnubAsyncUnsubscribeEnd allChannels() {
                pns.allChannels = true;
                return apiEnd;
            }

            @Override
            public PubnubAsyncUnsubscribeEnd channels(String[] channels) {
                pns.channels = channels;
                return apiEnd;
            }

            @Override
            public PubnubAsyncUnsubscribeEnd channel(String channel) {
                pns.channel = channel;
                return null;
            }

            @Override
            public PubnubAsyncUnsubscribeEnd allChannelGroups() {
                pns.allChannelGroups = true;
                return apiEnd;
            }

            @Override
            public PubnubAsyncUnsubscribeEnd channelGroup(String channelGroup) {
                pns.channelGroup = channelGroup;
                return apiEnd;
            }

            @Override
            public PubnubAsyncUnsubscribeEnd channelGroups(String[] channelGroups) {
                pns.channelGroups = channelGroups;
                return apiEnd;
            }
            
        };
        
        @Override
        public PubnubUnsubscribeAsyncChannel presence(boolean presence) {
            pns.presence = presence;
            return apiChannel;
        }
        
    };
    
    
    @Override
    public PubnubUnsubscribeAsyncPresence callback(UnsubscribeCallback callback) {
        pns.callback = callback;
        return apiPresence;
    }
    
}
