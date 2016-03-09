package com.pubnub.api;

public class PubnubPamAsync implements PubnubPamAsyncInterface {

    final Pubnub pubnub;
    
    PamAuditCallback auditCallback;
    PamModifyCallback modifyCallback;
    String channel;
    String channelGroup;
    String authKey;
    boolean read;
    boolean manage;
    boolean write;
    int ttl;
    
    
    PubnubPamAsync pns = this;
    
    
    PubnubPamAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void _invokeGrant() {
        
        if (channel != null) {
            pubnub.pamGrant(channel, authKey, read, write, modifyCallback);
        } else if (channelGroup != null) {
            pubnub._pamGrantChannelGroup(channelGroup, authKey, read, manage, ttl, modifyCallback, false);
        }
        
    }
    
    void _invokeAudit() {
        if (channel != null) {
            pubnub.pamAudit(channel, authKey, auditCallback);
        } else if (channelGroup != null) {
            pubnub.pamAuditChannelGroup(channelGroup, authKey, auditCallback);
        }  
    }
    
    void _invokeRevoke() {
        if (channel != null) {
            pubnub.pamGrant(channel, authKey, false, false, modifyCallback);
        } else if (channelGroup != null) {
            pubnub._pamGrantChannelGroup(channelGroup, authKey, false, false, ttl, modifyCallback, false);
        }
    }
    
    
    @Override
    public PubnubPamAsyncGrantInterface grant() {
        
        PubnubPamAsyncGrantApiStateCGOptions apiGrantCgOptions = new PubnubPamAsyncGrantApiStateCGOptions(){

            @Override
            public void invoke() {
                _invokeGrant();
            }

            @Override
            public PubnubPamAsyncGrantApiStateCGOptions read(boolean read) {
                pns.read = read;
                return this;
            }

            @Override
            public PubnubPamAsyncGrantApiStateCGOptions manage(boolean manage) {
                pns.manage = manage;
                return this;
            }

            @Override
            public PubnubPamAsyncGrantApiStateCGOptions ttl(int ttl) {
                pns.ttl = ttl;
                return this;
            }

            @Override
            public PubnubPamAsyncGrantApiStateCGOptions authKey(String authKey) {
                pns.authKey = authKey;
                return this;
            }
            
        };
        
        PubnubPamAsyncGrantApiStateCOptions apiGrantOptions = new PubnubPamAsyncGrantApiStateCOptions() {

            @Override
            public PubnubPamAsyncGrantApiStateCOptions read(boolean read) {
                pns.read = read;
                return this;
            }

            @Override
            public PubnubPamAsyncGrantApiStateCOptions write(boolean write) {
                pns.write = write;
                return this;
            }

            @Override
            public PubnubPamAsyncGrantApiStateCOptions ttl(int ttl) {
                pns.ttl = ttl;
                return this;
            }

            @Override
            public PubnubPamAsyncGrantApiStateCOptions authKey(String authKey) {
                pns.authKey = authKey;
                return this;
            }

            @Override
            public void invoke() {
                _invokeGrant();
            }
            
        };

        PubnubPamAsyncGrantApiStateChannel apiGrantChannel = new PubnubPamAsyncGrantApiStateChannel() {

            @Override
            public PubnubPamAsyncGrantApiStateCOptions channel(String channel) {
                pns.channel = channel;
                return apiGrantOptions;
                
            }

            @Override
            public PubnubPamAsyncGrantApiStateCGOptions channelGroup(String channelGroup) {
                pns.channelGroup = channelGroup;
                return apiGrantCgOptions;

            }
            
        };
        
        PubnubPamAsyncGrantInterface apiGrant = new PubnubPamAsyncGrantInterface() {

            @Override
            public PubnubPamAsyncGrantApiStateChannel callback(PamModifyCallback callback) {
                pns.modifyCallback = callback;
                return apiGrantChannel;
            }
            
        };
        return apiGrant;
    }

    @Override
    public PubnubPamAsyncRevokeInterface revoke() {
        
        PubnubPamAsyncRevokeEnd apiRevokeEnd = new PubnubPamAsyncRevokeEnd() {

            @Override
            public void invoke() {
                _invokeRevoke();
            }
        };
        
        PubnubPamAsyncRevokeApiStateOptions apiRevokeOptions = new PubnubPamAsyncRevokeApiStateOptions(){

            @Override
            public void invoke() {
                _invokeRevoke();
            }

            @Override
            public PubnubPamAsyncRevokeEnd authKey(String authKey) {
                pns.authKey = authKey;
                return apiRevokeEnd;
            }
            
        };
        
        PubnubPamAsyncRevokeApiStateChannel apiRevokeChannel = new PubnubPamAsyncRevokeApiStateChannel() {

            @Override
            public void invoke() {
                _invokeRevoke();
            }

            @Override
            public PubnubPamAsyncRevokeApiStateOptions channel(String channel) {
                pns.channel = channel;
                return apiRevokeOptions;
            }

            @Override
            public PubnubPamAsyncRevokeApiStateOptions channelGroup(String channelGroup) {
                pns.channelGroup = channelGroup;
                return apiRevokeOptions;
            }
            
        };
        
        PubnubPamAsyncRevokeInterface apiRevoke = new PubnubPamAsyncRevokeInterface() {

            @Override
            public PubnubPamAsyncRevokeApiStateChannel callback(PamModifyCallback callback) {
                pns.modifyCallback = callback;
                return apiRevokeChannel;
            }
            
        };
        return apiRevoke;
    }

    @Override
    public PubnubPamAsyncAuditInterface audit() {
        
        PubnubPamAsyncAuditEnd apiAuditEnd = new PubnubPamAsyncAuditEnd(){

            @Override
            public void invoke() {
                _invokeAudit();
            }
            
        };
        
        PubnubPamAsyncAuditApiStateCGOptions apiAuditCGOptions = new PubnubPamAsyncAuditApiStateCGOptions() {

            @Override
            public void invoke() {
                _invokeAudit();
            }

            @Override
            public PubnubPamAsyncAuditEnd authKey(String authKey) {
                pns.authKey = authKey;
                return apiAuditEnd;
            }
            
        };
        
        PubnubPamAsyncAuditApiStateCOptions apiAuditCOptions = new PubnubPamAsyncAuditApiStateCOptions() {

            @Override
            public void invoke() {
                _invokeAudit();  
            }

            @Override
            public PubnubPamAsyncAuditEnd authKey(String authKey) {
                pns.authKey = authKey;
                return apiAuditEnd;
            }
            
        };
        
        PubnubPamAsyncAuditApiStateChannel apiAuditChannel = new PubnubPamAsyncAuditApiStateChannel() {

            @Override
            public void invoke() {
                _invokeAudit();  
            }

            @Override
            public PubnubPamAsyncAuditApiStateCOptions channel(String channel) {
                pns.channel = channel;
                return apiAuditCOptions;
            }

            @Override
            public PubnubPamAsyncAuditApiStateCGOptions channelGroup(String channelGroup) {
                pns.channelGroup = channelGroup;
                return apiAuditCGOptions;
            }
            
        };
        
        PubnubPamAsyncAuditInterface apiAudit = new PubnubPamAsyncAuditInterface(){

            @Override
            public PubnubPamAsyncAuditApiStateChannel callback(PamAuditCallback callback) {
                pns.auditCallback = callback;
                return apiAuditChannel;
            }
            
        };
        return apiAudit;
    }
    
}
