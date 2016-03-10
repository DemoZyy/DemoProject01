package com.pubnub.api;

import org.json.JSONObject;

public class PubnubAsyncState implements PubnubStateAsyncInterface {



    final Pubnub pubnub;
    
    SetStateCallback setStateCallback;
    ChannelGroupStateCallback channelGroupStateCallback;
    ChannelStateCallback channelStateCallback;

    String channel;
    String channelGroup;

    
    String uuid;
    JSONObject state;

    
    PubnubAsyncState pns = this;
    
    
    PubnubAsyncState(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    
    @Override
    public PubnubStateAsyncApiGetC get() {
        
        PubnubStateAsyncApiGetEnd apiGetEnd = new PubnubStateAsyncApiGetEnd() {

            @Override
            public void get() {
                if (channel != null) {
                    pubnub.getState(channel, uuid, pns.channelStateCallback);
                } else if (channelGroup != null) {
                   //
                }
            }
            
        };
        
        PubnubStateAsyncApiGetCCb apiGetCCb = new PubnubStateAsyncApiGetCCb() {

            @Override
            public PubnubStateAsyncApiGetEnd callback(ChannelStateCallback callback) {
                pns.channelStateCallback = callback;
                return null;
            }
            
        };
        
        PubnubStateAsyncApiGetCgCb apiGetCgCb = new PubnubStateAsyncApiGetCgCb() {

            @Override
            public PubnubStateAsyncApiGetEnd callback(ChannelGroupStateCallback callback) {
                pns.channelGroupStateCallback = callback;
                return null;
            }
            
        };
        
        
        PubnubStateAsyncApiGetCUuid apiGetCUuid = new PubnubStateAsyncApiGetCUuid() {

            @Override
            public PubnubStateAsyncApiGetCCb uuid(String uuid) {
                pns.uuid = uuid;
                return apiGetCCb;
            }
            
        };
        
        PubnubStateAsyncApiGetCgUuid apiGetCgUuid = new PubnubStateAsyncApiGetCgUuid() {

            @Override
            public PubnubStateAsyncApiGetCgCb uuid(String uuid) {
                pns.uuid = uuid;
                return apiGetCgCb;
            }
            
        };
        
        
        PubnubStateAsyncApiGetC apiGet = new PubnubStateAsyncApiGetC() {

            @Override
            public PubnubStateAsyncApiGetCUuid channel(String channel) {
                pns.channel = channel;
                return apiGetCUuid;
            }
            /*
            @Override
            public PubnubStateAsyncApiGetCgUuid channelGroup(String channelGroup) {
                pns.channelGroup = channelGroup;
                return apiGetCgUuid;
            }
            */
            
        };
        return apiGet;
    }
    
    PubnubStateAsyncApiSetEnd apiSetEnd = new PubnubStateAsyncApiSetEnd() {

        @Override
        public void set() {
            if (channel != null) {
                pubnub.setState(channel, uuid, state, pns.setStateCallback);
                
            } else if (channelGroup != null) {
                pubnub.channelGroupSetState(channelGroup, uuid, state, pns.setStateCallback);
            }
        }
        
    };
    
    PubnubStateAsyncApiSetCCb setCCb = new PubnubStateAsyncApiSetCCb() {

        @Override
        public PubnubStateAsyncApiSetEnd callback(SetStateCallback callback) {
            pns.setStateCallback = callback;
            return apiSetEnd;
        }
        
    };
    
    
    
    PubnubStateAsyncApiSetCState setCState = new PubnubStateAsyncApiSetCState() {

        @Override
        public PubnubStateAsyncApiSetCCb state(JSONObject state) {
            pns.state = state;
            return setCCb;
        }
        
    };
    
    
    PubnubStateAsyncApiSetCgState setCgState = new PubnubStateAsyncApiSetCgState() {

        @Override
        public PubnubStateAsyncApiSetCCb state(JSONObject state) {
            pns.state = state;
            return setCCb;
        }
        
    };
    
    
    PubnubStateAsyncApiSetCUuid apiSetCUuuid = new PubnubStateAsyncApiSetCUuid() {

        @Override
        public PubnubStateAsyncApiSetCState uuid(String uuid) {
            pns.uuid = uuid;
            return setCState;
        }    
        
    };
    
    PubnubStateAsyncApiSetCgUuid apiSetCgUuid = new PubnubStateAsyncApiSetCgUuid() {

        @Override
        public PubnubStateAsyncApiSetCgState uuid(String uuid) {
            pns.uuid = uuid;
            return setCgState;
        }
        
    };
    
    @Override
    public PubnubStateAsyncApiSetC set() {
        PubnubStateAsyncApiSetC apiSet = new PubnubStateAsyncApiSetC() {

            @Override
            public PubnubStateAsyncApiSetCUuid channel(String channel) {
                pns.channel = channel;
                return apiSetCUuuid;
            }

            @Override
            public PubnubStateAsyncApiSetCgUuid channelGroup(String channelGroup) {
                pns.channelGroup = channelGroup;
                return apiSetCgUuid;
            }
            
        };
        return apiSet;
    }

}
