package com.pubnub.examples;

import com.pubnub.api.*;

class Class4 {
    Pubnub pubnub;
    
    Class4(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void start(String channel) {
        pubnub.addStreamListener(new StreamListener(){

            @Override
            public void streamStatus(StreamStatus status) {
                System.out.println(status);
                
            }

            @Override
            public void streamResult(StreamResult result) {
                System.out.println(result);
            }
            
        });
        try {
            pubnub.subscribe().onlyPresence().channel(channel).connect();
            
        } catch (PubnubException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


class Class3 {
    Pubnub pubnub;
    
    Class3(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void start(String channel) {
        pubnub.addStreamListener(new StreamListener(){

            @Override
            public void streamStatus(StreamStatus status) {
                System.out.println(status);
                
            }

            @Override
            public void streamResult(StreamResult result) {
                System.out.println(result);
            }
            
        });
        try {
            pubnub.subscribe().withPresence().channel(channel).connect();
            
        } catch (PubnubException e) {
            e.printStackTrace();
        }   
    }
}


class Class2 {
    Pubnub pubnub;
    
    Class2(Pubnub pubnub) {
        this.pubnub = pubnub;
    }
    
    void start(String channel) {
        pubnub.addStreamListener(new StreamListener(){

            @Override
            public void streamStatus(StreamStatus status) {
                System.out.println(status);
                
            }

            @Override
            public void streamResult(StreamResult result) {
                System.out.println(result);
            }
            
        });
        try {
            pubnub.subscribe().channel(channel).connect();
            
        } catch (PubnubException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


public class V4Example {

    public static void main(String[] args) {
        Pubnub pubnub =    new Pubnub.Builder()
                                     .setPublishKey("demo")
                                     .setSubscribeKey("demo")
                                     .build();
        
        new Class3(pubnub).start("ab");
        
        
    }
    
}
