package com.pubnub.examples;

import com.pubnub.api.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Scanner;
import static com.pubnub.examples.PubnubDemoConsoleHelpers.*;
import static java.lang.System.out;

public class PubnubDemoConsole {

    Pubnub pubnub;
    String publish_key = "demo";
    String subscribe_key = "demo";
    String secret_key = "";
    String cipher_key = "";
    boolean SSL  = true;
    Scanner reader;
    
    PubnubDemoConsole self = this;

    
    
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
        System.out.println(status);
        if (status.getCategory()  == StatusCategory.ACCESS_DENIED) {
            handlePamError(status);
        }
        
    }
    
    void handleNonErrorStatus(Status status) {
        System.out.println(status);
        if (status.getOperation() == OperationType.PUBLISH) {
            notifyUser("SUCCESS \n" + ((PublishStatus)status).getData().information + "\n" 
                    + ((PublishStatus)status).getData().timetoken);
        }
        
        
    }
    
    void handleStatus(Status status) {
        if (status.isError()) {
            handleErrorStatus((ErrorStatus)status);
        } else {
            handleNonErrorStatus(status);
        }
    }
    
    
    public PubnubDemoConsole(String publish_key, String subscribe_key, String secret_key, String cipher_key) {
        this.publish_key = publish_key;
        this.subscribe_key = subscribe_key;
        this.secret_key = secret_key;
        this.cipher_key = cipher_key;
    }

    public PubnubDemoConsole() {

    }

    private void notifyUser(Object message) {
        out.println(message.toString());
    }

    private void publish(String channel, boolean store) {
        notifyUser("Enter the message for publish. To exit loop enter QUIT");
        String message = "";

        PublishCallback cb = new PublishCallback() {

            @Override
            public void status(PublishStatus status) {
                self.handleStatus(status);
            }

        };

        while (true) {
            Hashtable args = new Hashtable(2);
            message = reader.nextLine();
            if (message.equalsIgnoreCase("QUIT")) {
                break;
            }

            try {
                Integer i = Integer.parseInt(message);
                pubnub.publish()
                .callback(cb)
                .channel(channel)
                .message(i)
                .storeInHistory(store)
                .send();
                continue;
            } catch (Exception e) {

            }
            try {
                Double d = Double.parseDouble(message);
                pubnub.publish()
                .callback(cb)
                .channel(channel)
                .message(d)
                .storeInHistory(store)
                .send();
                continue;
            } catch (Exception e) {

            }
            try {
                JSONArray js = new JSONArray(message);
                pubnub.publish()
                .callback(cb)
                .channel(channel)
                .message(js)
                .storeInHistory(store)
                .send();
                continue;
            } catch (Exception e) {

            }
            try {
                JSONObject js = new JSONObject(message);
                pubnub.publish()
                .callback(cb)
                .channel(channel)
                .message(js)
                .storeInHistory(store)
                .send();
                continue;
            } catch (Exception e) {

            }
            pubnub.publish()
            .callback(cb)
            .channel(channel)
            .message(message)
            .storeInHistory(store)
            .send();
        }

    }
    
    private void subscribe(final String channel) {

        try {
            /*
            pubnub.subscribe(channel, new Callback() {


            });
            */
        } catch (Exception e) {
        }
    }
    

    private void history(String channel, int count, boolean includeToken) {
        
        pubnub.history().callback(new HistoryCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(HistoryResult result) {
                notifyUser(result);
            }

        }).channel(channel)
        .count(count)
        .get();
        
    }

    private void hereNow(String channel) {
        boolean metadata = getBooleanFromConsole("Metadata");
        boolean uuids = getBooleanFromConsole("Return UUIDs");

        pubnub.hereNow().callback(new HereNowCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
                
            }

            @Override
            public void result(HereNowResult result) {
                notifyUser(result);
            }

        }).channel(channel).state(metadata).uuids(uuids).get();
        
    }

    private void unsubscribe(String channel) {
        
        pubnub.unsubscribe().callback(new UnsubscribeCallback(){

            @Override
            public void status(AcknowledgmentStatus status) {
                self.handleStatus(status);
            }
            
        }).presence(false).channel(channel).invoke();
    }

    private void unsubscribeFromGroup(String groupName) {
        pubnub.unsubscribe().callback(new UnsubscribeCallback(){

            @Override
            public void status(AcknowledgmentStatus status) {
                self.handleStatus(status);
            }
            
        }).presence(false).channelGroup(groupName).invoke();
    }


    private void time() {
        pubnub.time(new TimeCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(TimeResult result) {
                notifyUser(result);
            }

        });
    }

    private void disconnectAndResubscribe() {
        pubnub.disconnectAndResubscribe();

    }

    private void disconnectAndResubscribeWithTimetoken(String timetoken) {
        pubnub.disconnectAndResubscribeWithTimetoken(timetoken);

    }

    public void startDemo() {
        reader = new Scanner(System.in);
        notifyUser("HINT:\tTo test Re-connect and catch-up");
        notifyUser("\tDisconnect your machine from network/internet and");
        notifyUser("\tre-connect your machine after sometime");

        /*
        this.SSL = getBooleanFromConsole("SSL");

        if (this.publish_key.length() == 0)
            this.publish_key = getStringFromConsole("Publish Key");

        if (this.subscribe_key.length() == 0)
            this.subscribe_key = getStringFromConsole("Subscribe Key");

        if (this.secret_key.length() == 0)
            this.secret_key = getStringFromConsole("Secret Key", true);

        if (this.cipher_key.length() == 0)
            this.cipher_key = getStringFromConsole("Cipher Key", true);
        */
        
        pubnub = new Pubnub.Builder()
                .setPublishKey(this.publish_key)
                .setSubscribeKey(this.subscribe_key)
                .setSecretKey(this.secret_key)
                .setSsl(this.SSL)
                .setUuid("myuuid")
                .setAuthKey("myauthkey")
                .setOrigin("pubsub2.pubnub.com")
                .setCacheBusting(false)
                .build();
        
        displayMenuOptions();

        String channelName = null;
        int command = 0;
        while ((command = reader.nextInt()) != 9) {
            reader.nextLine();
            switch (command) {

            case 0:
                displayMenuOptions();
                break;

            case 1: {
                boolean isGroup = getBooleanFromConsole("Group");
                if (isGroup) {
                    String groupName = getStringFromConsole("Subscribe: Enter Group name");
                    subscribeToGroup(groupName);
                    /*
                     * notifyUser("Subscribed to following groups: ");
                     * notifyUser(PubnubUtil.joinString(
                     * pubnub.getSubscribedGroupsArray(), " : "));
                     */
                } else {
                    channelName = getStringFromConsole("Subscribe: Enter Channel name");
                    subscribe(channelName);

                    notifyUser("Subscribed to following channels: ");
                    notifyUser(PubnubUtil.joinString(pubnub.getSubscribedChannelsArray(), " : "));
                }
            }
                break;
            case 2:
                channelName = getStringFromConsole("Channel Name");
                boolean store = getBooleanFromConsole("Store", true);
                publish(channelName, store);
                break;
            case 3:
                channelName = getStringFromConsole("Channel Name");
                //presence(channelName);
                break;
            case 4:
                channelName = getStringFromConsole("Channel Name");
                int count = getIntFromConsole("Count");
                boolean includeToken = getBooleanFromConsole("Include Timetokens");
                history(channelName, count, includeToken);
                break;
            case 5:
                channelName = getStringFromConsole("Channel Name", true);
                hereNow(channelName);
                break;
            case 6: {
                boolean isGroup = getBooleanFromConsole("Group");
                if (isGroup) {
                    String groupName = getStringFromConsole("UnSubscribe: Enter Group name");
                    unsubscribeFromGroup(channelName);
                } else {
                    channelName = getStringFromConsole("UnSubscribe: Enter Channel name");
                    unsubscribe(channelName);
                }
            }
                break;
            case 7:
                /*
                channelName = getStringFromConsole("Channel Name");
                unsubscribePresence(channelName);
                */
                break;
            case 8:
                time();
                break;
            case 10:
                disconnectAndResubscribe();
                break;
            case 11:
                notifyUser("Disconnect and Resubscribe with timetoken : Enter timetoken");
                String timetoken = getStringFromConsole("Timetoken");
                disconnectAndResubscribeWithTimetoken(timetoken);
                break;
            case 18:
                notifyUser("Set/Unset Auth Key: Enter blank for unsetting key");
                String authKey = getStringFromConsole("Auth Key");
                pubnub.setAuthKey(authKey);
                break;
            case 19:
                pamGrant();
                break;
            case 20:
                pamRevoke();
                break;
            case 21:
                pamAudit();
                break;
            case 26:
                notifyUser("Set UUID");
                String uuid = getStringFromConsole("UUID");
                pubnub.setUUID(uuid);
                break;
            case 27:
                int heartbeat = getIntFromConsole("Pubnub Presence Heartbeat ( in seconds ), Current value : "
                        + pubnub.getHeartbeat());
                /*
                pubnub.setHeartbeat(heartbeat, new Callback() {

                });
                */
                break;
            case 28:
                int heartbeatInterval = getIntFromConsole("Pubnub Presence Heartbeat Interval ( in seconds ), Current value : "
                        + pubnub.getHeartbeatInterval());
                /*
                pubnub.setHeartbeatInterval(heartbeatInterval, new Callback() {

                });
                */
                break;
            case 29:
                getState();
                break;
            case 30:
                setState();
                break;
            case 31:
                String uid = getStringFromConsole("UUID", true);
                if (uid == null || uid.length() == 0)
                    uid = pubnub.getUUID();
                whereNow(uid);
                break;
            case 32:
            // add channel to channel group
            {
                String group = getStringFromConsole("Group");
                String channel = getStringFromConsole("Channel");
                addChannelToGroup(group, channel);
            }
                break;
            case 33:
            // remove channel from group
            {
                String group = getStringFromConsole("Group");
                String channel = getStringFromConsole("Channel");
                removeChannelFromGroup(group, channel);
            }
                break;
            case 34:
            // list channels for channel group
            {
                String group = getStringFromConsole("Group");
                listChannelsForGroup(group);
            }
                break;
            case 35:
            // list groups
            {
                listGroups();

            }
                break;
            case 36:
            // remove group
            {
                String group = getStringFromConsole("Group");
                removeGroup(group);
            }
                break;

            default:
                notifyUser("Invalid Input");
            }
            displayMenuOptions();
        }
        notifyUser("Exiting");
        pubnub.shutdown();

    }

    private void subscribeToGroup(String groupName) {

        try {
            /*
            pubnub.channelGroupSubscribe(groupName, new Callback() {

                @Override
                public void connectCallback(String channel, Object message) {
                    notifyUser("SUBSCRIBE : CONNECT on channel:" + channel + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    notifyUser("SUBSCRIBE : DISCONNECT on channel:" + channel + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    notifyUser("SUBSCRIBE : RECONNECT on channel:" + channel + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    notifyUser("SUBSCRIBE : " + channel + " : " + message.getClass() + " : " + message.toString());

                }

                @Override
                public void errorCallback(String channel, PubnubError error) {

                    notifyUser("SUBSCRIBE : ERROR on channel " + channel + " : " + error.toString());
                    if (error.errorCode == PubnubError.PNERR_TIMEOUT)
                        pubnub.disconnectAndResubscribe();
                }
            });
            */
        } catch (Exception e) {
        }

    }



    private void removeGroup(String group) {
        
        
       pubnub.channelGroup().removeGroup().callback(new ChannelGroupChangeCallback() {

           @Override
           public void status(AcknowledgmentStatus status) {
               self.handleStatus(status);
           }

       }).channelGroup(group).remove();


    }

    private void listGroups() {

        
        pubnub.channelGroup().listGroups().callback(new GroupAuditCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(ChannelGroupsResult result) {
                notifyUser(result);
            }

        }).list();


    }

    private void listChannelsForGroup(String group) {
        
        pubnub.channelGroup().listChannels().callback( new GroupChannelsAuditCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(ChannelGroupChannelsResult result) {
                notifyUser(result);
            }

        }).channelGroup(group).list();
        
    }

    private void removeChannelFromGroup(String group, String channel) {
        
        pubnub.channelGroup().removeChannel().callback(new ChannelGroupChangeCallback() {

            @Override
            public void status(AcknowledgmentStatus status) {
                self.handleStatus(status);
            }

        }).channelGroup(group).channel(channel).remove();
        
    }

    private void addChannelToGroup(String group, String channel) {
        
        pubnub.channelGroup().addChannel().callback(new ChannelGroupChangeCallback() {

            @Override
            public void status(AcknowledgmentStatus status) {
                self.handleStatus(status);
            }

        }).channelGroup(group).channel(channel).add();
        
    }

    private void whereNow(String uuid) {
        
        pubnub.whereNow().callback(new WhereNowCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(WhereNowResult result) {
                notifyUser(result);
            }

        }).uuid(uuid).get();
        
    }

    private void setState() {
        boolean isGroup = getBooleanFromConsole("Group");
        if (!isGroup) {
            String channel = getStringFromConsole("Channel");
            String uuid = getStringFromConsole("UUID", true);
            if (uuid == null || uuid.length() == 0)
                uuid = pubnub.getUUID();
            JSONObject metadata = getJSONObjectFromConsole("Metadata");
            
            
            pubnub.state().set().channel(channel).uuid(uuid).state(metadata)
            .callback(new SetStateCallback() {

                @Override
                public void status(ClientStateUpdateStatus status) {
                    self.handleStatus(status);
                }

            }).set();
            
            
            
        } else {
            String group = getStringFromConsole("Group");
            String uuid = getStringFromConsole("UUID", true);
            if (uuid == null || uuid.length() == 0)
                uuid = pubnub.getUUID();
            JSONObject metadata = getJSONObjectFromConsole("Metadata");
            
            
            pubnub.state().set().channelGroup(group).uuid(uuid).state(metadata)
            .callback(new SetStateCallback() {

                @Override
                public void status(ClientStateUpdateStatus status) {
                    self.handleStatus(status);
                }

            }).set();
            
        }
    }

    private void getState() {
        String channel = getStringFromConsole("Channel");
        String uuid = getStringFromConsole("UUID", true);
        if (uuid == null || uuid.length() == 0)
            uuid = pubnub.getUUID();
        
        
        pubnub.state().get().channel(channel).uuid(uuid).callback(new ChannelStateCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(ChannelClientStateResult result) {
                notifyUser(result);
            }

        }).get();
        
    }


    private void pamGrant() {
        String channel = getStringFromConsole("Channel");
        String auth_key = getStringFromConsole("Auth Key");
        boolean read = getBooleanFromConsole("Read");
        boolean write = getBooleanFromConsole("Write");
        int ttl = getIntFromConsole("TTL");
        
        pubnub.pam().grant().callback( new PamModifyCallback() {

            @Override
            public void status(AcknowledgmentStatus status) {
                self.handleStatus(status);
            }


        })
        .channel(channel)
        .authKey(auth_key)
        .read(read)
        .write(write)
        .ttl(ttl)
        .invoke();
        
    }

    private void pamAudit() {
        String channel = getStringFromConsole("Channel", true);
        String auth_key = getStringFromConsole("Auth Key", true);
        
        PamAuditCallback cb = new PamAuditCallback() {

            @Override
            public void status(ErrorStatus status) {
                self.handleStatus(status);
            }

            @Override
            public void result(PamAuditResult result) {
                notifyUser(result);
            }

        };

        if (channel != null && channel.length() > 0) {
            if (auth_key != null && auth_key.length() != 0) {
                pubnub.pam().audit().callback(cb).channel(channel).authKey(auth_key).invoke();
            } else {
                pubnub.pam().audit().callback(cb).channel(channel).invoke();
            }
        } else {
            pubnub.pam().audit().callback(cb).invoke();
        }
        
    }

    private void pamRevoke() {
        String channel = getStringFromConsole("Enter Channel");
        String auth_key = getStringFromConsole("Auth Key");

        pubnub.pam().revoke().callback(new PamModifyCallback() {

            @Override
            public void status(AcknowledgmentStatus status) {
                self.handleStatus(status);
            }

        })
        .channel(channel)
        .authKey(auth_key)
        .invoke();
        
    }

    private void displayMenuOptions() {
        notifyUser("ENTER 1  FOR Subscribe " + "(Currently subscribed to "
                + this.pubnub.getCurrentlySubscribedChannelNames() + ")");
        notifyUser("ENTER 2  FOR Publish");
        notifyUser("ENTER 3  FOR Presence");
        notifyUser("ENTER 4  FOR History");
        notifyUser("ENTER 5  FOR Here Now");
        notifyUser("ENTER 6  FOR Unsubscribe");
        notifyUser("ENTER 7  FOR Presence-Unsubscribe");
        notifyUser("ENTER 8  FOR Time");
        notifyUser("ENTER 9  FOR EXIT OR QUIT");
        notifyUser("ENTER 10 FOR Disconnect-And-Resubscribe");
        notifyUser("ENTER 11 FOR Disconnect-And-Resubscribe with timetoken");
        notifyUser("ENTER 18 FOR Setting/Unsetting auth key ( current: " + pubnub.getAuthKey() + " )");
        notifyUser("ENTER 19 FOR PAM grant");
        notifyUser("ENTER 20 FOR PAM revoke");
        notifyUser("ENTER 21 FOR PAM Audit");
        notifyUser("ENTER 27 FOR Setting Presence Heartbeat ( current: " + pubnub.getHeartbeat() + " )");
        notifyUser("ENTER 28 FOR Setting Presence Heartbeat Interval ( current: " + pubnub.getHeartbeatInterval()
                + " )");
        notifyUser("ENTER 29 FOR Getting Subscriber State");
        notifyUser("ENTER 30 FOR Setting Subscriber State");
        notifyUser("ENTER 31 FOR Where Now");
        notifyUser("ENTER 32 FOR [Channel Group] Add Channel");
        notifyUser("ENTER 33 FOR [Channel Group] Remove Channel");
        notifyUser("ENTER 34 FOR [Channel Group] List Channels");
        notifyUser("ENTER 35 FOR [Channel Group] List Groups");
        notifyUser("ENTER 36 FOR [Channel Group] Remove Group");
        notifyUser("ENTER 37 FOR [Channel Group] List Namespaces");
        notifyUser("ENTER 38 FOR [Channel Group] Remove Namespace");
        notifyUser("\nENTER 0 to display this menu");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PubnubDemoConsole pdc;
        if (args.length == 4 || true) {
            //pdc = new PubnubDemoConsole(args[0], args[1], args[2], args[3]);
            pdc = new PubnubDemoConsole("demo", "demo", "", "");
        } else {
            // = new PubnubDemoConsole();
        }
        pdc.startDemo();
    }

}
