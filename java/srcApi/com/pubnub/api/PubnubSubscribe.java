package com.pubnub.api;

import java.util.Hashtable;

public class PubnubSubscribe implements PubnubSubscribeInterface {
    
    
    private Pubnub pubnub;
    private Callback callback;
    private String channel;
    private String[] channels;
    private String channelGroup;
    private String[] channelGroups;
    private String filter;
    private String timetoken;
    PubnubSubscribe pns = this;
    
    
    void _connect() throws PubnubException {
        Hashtable args = new Hashtable();
    
        if (channel != null && channels == null) {
            channels = new String[]{channel};
        }
    
    
        if (channelGroup != null && channelGroups == null) {
            channelGroups = new String[]{channelGroup};
        }
    
        addToHash(args, "channels", channels);
        addToHash(args, "groups", channelGroups);
        addToHash(args, "callback", callback);
        addToHash(args, "timetoken", timetoken);
        addToHash(args, "filter", filter);
    
        if (pubnub != null)
            pubnub.subscribe(args);
        else 
            throw new PubnubException("Pubnub is Null");
    }
    
    void addToHash(Hashtable h, String name, Object object) {
        if (object != null) {
            h.put(name, object);
        }
    }
    
    PubnubSubscribe(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    PubnubSubscribeApiState1 i1 = new PubnubSubscribeApiState1(){

        @Override
        public PubnubSubscribeApiState7 filter(String filter) {
            pns.filter = filter;
            return i7;
        }

        @Override
        public PubnubSubscribeApiState8 timeToken(String timetoken) {
            pns.timetoken = timetoken;
            return i8;
        }

        @Override
        public void connect() throws PubnubException {
            pns._connect();
        }

        @Override
        public PubnubSubscribeApiState3 channelGroup(String channelGroup) {
            pns.channelGroup = channelGroup;
            return i3;
        }

        @Override
        public PubnubSubscribeApiState3 channelGroups(String[] channelGroups) {
            pns.channelGroups = channelGroups;
            return i3;
        }
        
    };
    
    PubnubSubscribeApiState2 i2 = new PubnubSubscribeApiState2(){

        @Override
        public PubnubSubscribeApiState7 filter(String filter) {
            pns.filter = filter;
            return i7;
        }

        @Override
        public PubnubSubscribeApiState8 timeToken(String timetoken) {
            pns.timetoken = timetoken;
            return i8;
        }

        @Override
        public void connect() throws PubnubException {
            pns._connect();
        }

        @Override
        public PubnubSubscribeApiState3 channel(String channel) {
            pns.channel = channel;
            return i3;
        }

        @Override
        public PubnubSubscribeApiState3 channels(String[] channels) {
            pns.channels = channels;
            return i3;
        }
        
    };
    
    PubnubSubscribeApiState3 i3 = new PubnubSubscribeApiState3() {

        @Override
        public void connect() throws PubnubException {
            pns._connect();
        }

        @Override
        public PubnubSubscribeApiState7 filter(String filter) {
            pns.filter = filter;
            return i7;
        }

        @Override
        public PubnubSubscribeApiState8 timeToken(String timetoken) {
            pns.timetoken = timetoken;
            return i8;
        }
        
    };
    
    PubnubSubscribeApiState7 i7 = new PubnubSubscribeApiState7() {

        @Override
        public void connect() throws PubnubException {
            pns._connect();
        }

        @Override
        public PubnubSubscribeEnd timeToken(String timetoken) {
            pns.timetoken = timetoken;
            return pubnubSubscribeEnd;
        }
        
    };
    
    PubnubSubscribeApiState8 i8 = new PubnubSubscribeApiState8() {

        @Override
        public void connect() throws PubnubException {
            pns._connect();
            
        }

        @Override
        public PubnubSubscribeEnd filter(String filter) {
            pns.filter = filter;
            return pubnubSubscribeEnd;
        }
        
    };
    
    PubnubSubscribeEnd pubnubSubscribeEnd = new PubnubSubscribeEnd(){

        @Override
        public void connect() throws PubnubException {
            pns._connect();
        }
        
    };
    
    PubnubSubscribeApiState i = new PubnubSubscribeApiState(){

        @Override
        public PubnubSubscribeApiState1 channel(String channel) {
            pns.channel = channel;
            return i1;
        }

        @Override
        public PubnubSubscribeApiState1 channels(String[] channels) {
            pns.channels = channels;
            return i1;
        }

        @Override
        public PubnubSubscribeApiState2 channelGroup(String channelGroup) {
            pns.channelGroup = channelGroup;
            return i2;
        }

        @Override
        public PubnubSubscribeApiState2 channelGroups(String[] channelGroups) {
            pns.channelGroups = channelGroups;
            return i2;
        }
        
    };

    @Override
    public PubnubSubscribeApiState callback(Callback callback) {
        this.callback = callback;
        return i;
    }

}
