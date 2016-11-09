package com.pubnub.api.workers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.PubNubUtil;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.managers.ListenerManager;
import com.pubnub.api.models.consumer.PNErrorData;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.server.PresenceEnvelope;
import com.pubnub.api.models.server.PublishMetaData;
import com.pubnub.api.models.server.SubscribeMessage;
import com.pubnub.api.vendor.Crypto;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;


@Slf4j
public class SubscribeMessageWorker implements Runnable {

    private PubNub pubnub;
    private ListenerManager listenerManager;
    private LinkedBlockingQueue<SubscribeMessage> queue;

    private boolean isRunning;

    public SubscribeMessageWorker(PubNub pubnubInstance, ListenerManager listenerManagerInstance, LinkedBlockingQueue<SubscribeMessage> queueInstance) {
        this.pubnub = pubnubInstance;
        this.listenerManager = listenerManagerInstance;
        this.queue = queueInstance;
    }

    @Override
    public void run() {
        takeMessage();
    }


    private void takeMessage() {
        this.isRunning = true;

        while (this.isRunning) {
            try {
                this.processIncomingPayload(this.queue.take());
            } catch (InterruptedException e) {
                this.isRunning = false;
                log.warn("take message interrupted", e);
            }
        }
    }

    private JsonElement processMessage(final JsonElement input) {
        // if we do not have a crypto key, there is no way to process the node; let's return.
        if (pubnub.getConfiguration().getCipherKey() == null) {
            return input;
        }

        Crypto crypto = new Crypto(pubnub.getConfiguration().getCipherKey());
        String inputText;
        String outputText;
        JsonElement outputObject;

        if (input.isJsonObject() && input.getAsJsonObject().has("pn_other")) {
            inputText = input.getAsJsonObject().get("pn_other").getAsString();
        } else {
            inputText = input.getAsString();
        }

        try {
            outputText = crypto.decrypt(inputText);
        } catch (PubNubException e) {
            PNStatus pnStatus = PNStatus.builder().error(true)
                    .errorData(new PNErrorData(e.getMessage(), e))
                    .operation(PNOperationType.PNSubscribeOperation)
                    .category(PNStatusCategory.PNDecryptionErrorCategory)
                    .build();

            listenerManager.announce(pnStatus);
            return null;
        }

        outputObject = this.pubnub.getGsonParser().fromJson(outputText, JsonElement.class);

        // inject the decoded response into the payload
        if (input.isJsonObject() && input.getAsJsonObject().has("pn_other")) {
            JsonObject objectNode = input.getAsJsonObject();
            objectNode.add("pn_other", outputObject);
            outputObject = objectNode;
        }

        return outputObject;
    }

    private void processIncomingPayload(final SubscribeMessage message) {
        String channel = message.getChannel();
        String subscriptionMatch = message.getSubscriptionMatch();
        PublishMetaData publishMetaData = message.getPublishMetaData();

        if (channel != null && channel.equals(subscriptionMatch)) {
            subscriptionMatch = null;
        }

        if (message.getChannel().endsWith("-pnpres")) {
            PresenceEnvelope presencePayload = this.pubnub.getGsonParser().fromJson(message.getPayload(), PresenceEnvelope.class);

            String strippedPresenceChannel = null;
            String strippedPresenceSubscription = null;

            if (channel != null) {
                strippedPresenceChannel = PubNubUtil.replaceLast(channel, "-pnpres", "");
            }
            if (subscriptionMatch != null) {
                strippedPresenceSubscription = PubNubUtil.replaceLast(subscriptionMatch, "-pnpres", "");
            }

            PNPresenceEventResult pnPresenceEventResult = PNPresenceEventResult.builder()
                    .event(presencePayload.getAction())
                    .channel(strippedPresenceChannel)
                    .subscription(strippedPresenceSubscription)
                    .state(presencePayload.getData())
                    .timetoken(publishMetaData.getPublishTimetoken())
                    .occupancy(presencePayload.getOccupancy())
                    .uuid(presencePayload.getUuid())
                    .timestamp(presencePayload.getTimestamp())
                    .build();

            listenerManager.announce(pnPresenceEventResult);
        } else {
            JsonElement extractedMessage = processMessage(message.getPayload());

            if (extractedMessage == null) {
                log.debug("unable to parse payload on #processIncomingMessages");
            }

            PNMessageResult pnMessageResult = PNMessageResult.builder()
                    .message(extractedMessage)
                    .channel(channel)
                    .subscription(subscriptionMatch)
                    .timetoken(publishMetaData.getPublishTimetoken())
                    .build();


            listenerManager.announce(pnMessageResult);
        }
    }

}
