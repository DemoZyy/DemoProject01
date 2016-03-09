package com.pubnub.api;

public class PubnubWhereNowAsync implements PubnubWhereNowAsyncInterface {

    private Pubnub pubnub;
    private WhereNowCallback callback;
    private String uuid;


    PubnubWhereNowAsync pns = this;
    
    
    PubnubWhereNowAsync(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    
    void _get() {
        if (pubnub != null) {
            if (uuid != null) {
                pubnub.whereNow(uuid, callback);
            } else {
                pubnub.whereNow(callback);
            }
        }      
    }
    
    PubnubWhereNowAsyncEnd apiStateEnd = new PubnubWhereNowAsyncEnd() {

        @Override
        public void get() {
            _get();
        }
        
    };
    
    PubnubWhereNowAsyncApiStateUuid apitStateUuid = new PubnubWhereNowAsyncApiStateUuid(){

        @Override
        public PubnubWhereNowAsyncEnd uuid(String uuid) {
            pns.uuid = uuid;
            return apiStateEnd;
        }

        @Override
        public void get() {
            _get();
        }
        
    };
    
    
    @Override
    public PubnubWhereNowAsyncApiStateUuid callback(WhereNowCallback callback) {
        pns.callback = callback;
        return apitStateUuid;
    }

    
    
}
