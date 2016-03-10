package com.pubnub.api;

import org.json.JSONObject;

public class PubnubPushNAsync implements PubnubPushNAsyncInterface {


    final Pubnub pubnub;
    
    PushNotificationsChannelsAuditCallback pushNotificationsChannelsAuditCallback;
    PushNotificationsChangeCallback pushNotificationsChangeCallback;

    boolean disable;
    boolean enable;
    boolean remove;
    boolean get;
    
    String channel;
    String[] channels;

    String registrationId;

    PubnubPushNAsync pns = this;
    
    
    PubnubPushNAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    
    
    @Override
    public PubnubPushNAsyncApiStateDisableC disableOn() {
        
        PubnubPushNAsyncApiStateDisableRegId apiRegId = new PubnubPushNAsyncApiStateDisableRegId() {

            PubnubPushNAsyncApiStateDisableCb apiCb = new PubnubPushNAsyncApiStateDisableCb() {

                PubnubPushNAsyncApiStateDisableEnd apiEnd = new PubnubPushNAsyncApiStateDisableEnd(){

                    @Override
                    public void disable() {
                        if (channel != null) {
                            pubnub.disablePushNotificationsOnChannel(
                                    channel, registrationId, pns.pushNotificationsChangeCallback);
                        } else if (channels != null) {
                            pubnub.disablePushNotificationsOnChannels(
                                    channels, registrationId, pns.pushNotificationsChangeCallback);                            
                        }
                        
                    }
                    
                };
                
                @Override
                public PubnubPushNAsyncApiStateDisableEnd callback(PushNotificationsChangeCallback callback) {
                    pns.pushNotificationsChangeCallback = callback;
                    return apiEnd;
                }
                
            };
            
            @Override
            public PubnubPushNAsyncApiStateDisableCb registrationId(String registrationId) {
                pns.registrationId = registrationId;
                return apiCb;
            }
            
        };
        
        PubnubPushNAsyncApiStateDisableC apiDisable = new PubnubPushNAsyncApiStateDisableC() {

            @Override
            public PubnubPushNAsyncApiStateDisableRegId channel(String channel) {
                pns.channel = channel;
                return apiRegId;
            }

            @Override
            public PubnubPushNAsyncApiStateDisableRegId channels(String[] channels) {
                pns.channels = channels;
                return apiRegId;
            }
            
        };

        return apiDisable;
    }

    @Override
    public PubnubPushNAsyncApiStateEnableC enableOn() {
        PubnubPushNAsyncApiStateEnableC apiEnable = new PubnubPushNAsyncApiStateEnableC() {

            PubnubPushNAsyncApiStateEnableRegId apiRegId = new PubnubPushNAsyncApiStateEnableRegId() {

                PubnubPushNAsyncApiStateEnableCb apiCb = new PubnubPushNAsyncApiStateEnableCb(){

                    PubnubPushNAsyncApiStateEnableEnd apiEnd = new PubnubPushNAsyncApiStateEnableEnd() {

                        @Override
                        public void enable() {
                            if (channel != null) {
                                pubnub.enablePushNotificationsOnChannel(
                                        channel, registrationId, pns.pushNotificationsChangeCallback);
                            } else if (channels != null) {
                                pubnub.enablePushNotificationsOnChannels(
                                        channels, registrationId, pns.pushNotificationsChangeCallback);                            
                            }
                            
                        }
                        
                    };
                    
                    @Override
                    public PubnubPushNAsyncApiStateEnableEnd callback(PushNotificationsChangeCallback callback) {
                        pns.pushNotificationsChangeCallback = callback;
                        return apiEnd;
                    }
                    
                };
                
                @Override
                public PubnubPushNAsyncApiStateEnableCb registrationId(String registrationId) {
                    pns.registrationId = registrationId;
                    return null;
                }
                
            };
            
            @Override
            public PubnubPushNAsyncApiStateEnableRegId channel(String channel) {
                pns.channel = channel;
                return apiRegId;
            }

            @Override
            public PubnubPushNAsyncApiStateEnableRegId channels(String[] channels) {
                pns.channels = channels;
                return apiRegId;
            }
            
        };
        return apiEnable;
    }

    @Override
    public PubnubPushNAsyncApiStateGetCRegId getChannels() {
        PubnubPushNAsyncApiStateGetCRegId apiGet = new PubnubPushNAsyncApiStateGetCRegId() {

            PubnubPushNAsyncApiStateGetCCb apiCb = new PubnubPushNAsyncApiStateGetCCb() {

                PubnubPushNAsyncApiStateGetCEnd apiEnd = new PubnubPushNAsyncApiStateGetCEnd() {

                    @Override
                    public void get() {
                        pubnub.requestPushNotificationEnabledChannelsForDeviceRegistrationId
                        (registrationId, pushNotificationsChannelsAuditCallback);
                    }
                    
                };
                
                @Override
                public PubnubPushNAsyncApiStateGetCEnd callback(PushNotificationsChannelsAuditCallback callback) {
                    pns.pushNotificationsChannelsAuditCallback = callback;
                    return apiEnd;
                }
                
            };
            
            @Override
            public PubnubPushNAsyncApiStateGetCCb registrationId(String registrationId) {
                pns.registrationId = registrationId;
                return apiCb;
            }
            
        };
        return apiGet;
    }

    @Override
    public PubnubPushNAsyncApiStateRemoveCRegId removeAll() {
        PubnubPushNAsyncApiStateRemoveCRegId apiRemove = new PubnubPushNAsyncApiStateRemoveCRegId(){

            PubnubPushNAsyncApiStateRemoveCCb apiCb = new PubnubPushNAsyncApiStateRemoveCCb(){

                PubnubPushNAsyncApiStateRemoveEnd apiEnd = new PubnubPushNAsyncApiStateRemoveEnd(){

                    @Override
                    public void remove() {
                        pubnub.
                        removeAllPushNotificationsForDeviceRegistrationId
                        (registrationId, pushNotificationsChangeCallback);
                    }
                    
                };
                
                @Override
                public PubnubPushNAsyncApiStateRemoveEnd callback(PushNotificationsChangeCallback callback) {
                    pns.pushNotificationsChangeCallback = callback;
                    return apiEnd;
                }
                
            };
            
            @Override
            public PubnubPushNAsyncApiStateRemoveCCb registrationId(String registrationId) {
                pns.registrationId = registrationId;
                return apiCb;
            }
            
        };
        return apiRemove;
    }

}
