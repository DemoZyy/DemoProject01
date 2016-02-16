package com.pubnub.examples;

import org.json.JSONException;

import com.pubnub.api.*;

public class PubnubPublish {

    PubnubPublish self = this;
    
    Pubnub pubnub =    new Pubnub.Builder()
            .setPublishKey("demo")
            .setSubscribeKey("demo")
            .build();
    
    Pubnub pubnub_pam =    new Pubnub.Builder()
            .setPublishKey("pam")
            .setSubscribeKey("pam")
            .setSecretKey("pam")
            .build();
    
    void reconfigOnPAMError(ErrorStatus status) {
        if (status.getOperation() == OperationType.SUBSCRIBE) {
            //
        }
        if (status.getOperation() == OperationType.HISTORY) {
            System.out.println("RECONFIG");
            status.retry();
        }
    }
    
    
    void handlePamError(ErrorStatus status) {
        String pamResourceName = 
                (status.getErrorData().getChannels() != null)?
                status.getErrorData().getChannels()[0] : status.getErrorData().getChannelGroups()[0];
                
        String pamResourceType = (status.getErrorData().getChannels() != null)?"channel":"channel-groups";
        
        if (status.getOperation() == OperationType.PUBLISH) {
            //System.out.printf("^^^^ Error publishing with authKey: %s to channel %s.", _authKey, pamResourceName);
            System.out.println("^^^^ Setting auth to an authKey that will allow for both sub and pub");
        }
        if (status.getOperation() == OperationType.SUBSCRIBE) {
            //
        }
        
        if (status.getOperation() == OperationType.HISTORY) {
            System.out.println("^^^^ Setting auth to an authKey that will allow history operations");
        }
        
        reconfigOnPAMError(status);
        
    }
    
    void handleErrorStatus(ErrorStatus status) {
        
        if (status.getCategory()  == StatusCategory.ACCESS_DENIED) {
            handlePamError(status);
        }
        
    }
    
    void handleNonErrorStatus(Status status) {
        
        if (status.getCategory() == StatusCategory.ACK) {
            
        }
        
        if (status.getOperation() == OperationType.SUBSCRIBE) {
            
        }
        
        
        
    }
    
    void handleStatus(Status status) {
        if (status.isError()) {
            handleErrorStatus((ErrorStatus)status);
        } else {
            handleNonErrorStatus(status);
        }
    }
    
    void publish() {
        
        /*
        pubnub.publish("ab", "bc", new PublishCallback(){

            @Override
            public void status(PublishStatus status) {
                if (!status.isError()) {
                    System.out.println("Message sent at TT: " + status.getData().timetoken);
                } else {
                    self.handleStatus((Status)status);
                }
            }
            
        });
        */
        /*
        pubnub.history("a", 10, new HistoryCallback(){
            @Override
            public void result(HistoryResult result) {
                try {
                    System.out.println("Loaded history data: " + 
                            result.getData().getMessages().toString(2) + "\nwith start " + 
                            result.getData().getStart() + "\nand end " + result.getData().getEnd());
                } catch (JSONException e) {
                }
            }
            @Override
            public void status(ErrorStatus status) {
                self.handleStatus((Status)status);
            }
        });
        */
        
        
        /*
        pubnub_pam.history("a", 10, new HistoryCallback(){
            @Override
            public void result(HistoryResult result) {
                System.out.println("Loaded history data: " + 
                        result.getData().getMessages() + "with start " + 
                        result.getData().getStart() + " and" + result.getData().getEnd());
            }
            @Override
            public void status(ErrorStatus status) {
                System.out.println(status);
               //self.handleStatus(status);
            }
        });
        */
        /*
        pubnub.hereNow("a", new HereNowCallback(){
            @Override
            public void result(HereNowResult result) {
                System.out.println("^^^^ Loaded hereNowForChannel data: occupancy: " + 
                        result.getData().getOccupancy() + 
                        ", uuids: " + PubnubUtil.joinString(result.getData().getUuids(), ","));
            }
            @Override
            public void status(ErrorStatus status) {
                System.out.println(status);
                //self.handleStatus((Status)status);
            }
        });
        */
        pubnub_pam.hereNow("a", new HereNowCallback(){
            @Override
            public void result(HereNowResult result) {
                System.out.println("^^^^ Loaded hereNowForChannel data: occupancy: " + 
                        result.getData().getOccupancy() + 
                        ", uuids: " + PubnubUtil.joinString(result.getData().getUuids(), ","));
            }
            @Override
            public void status(ErrorStatus status) {
                System.out.println(status);
                //self.handleStatus((Status)status);
            }
        });
 
    }
    
    
    public static void main(String[] args) {
        new PubnubPublish().publish();
    }

}
