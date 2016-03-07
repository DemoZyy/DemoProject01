package com.pubnub.api;

public class AcknowledgmentStatus extends ErrorStatus {
    AcknowledgmentStatus(Result result) {
        super(result);
    }

    AcknowledgmentStatus() {
        super();
    }
    
    AcknowledgmentStatus(ErrorStatus s) {
        super();
    }
    
}
