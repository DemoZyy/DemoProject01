package com.pubnub.api.endpoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.builder.PubNubErrorBuilder;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.vendor.Crypto;
import lombok.Setter;
import lombok.experimental.Accessors;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Accessors(chain = true, fluent = true)
public class History extends Endpoint<JsonElement, PNHistoryResult> {
    private static final int MAX_COUNT = 100;
    @Setter
    private String channel;
    @Setter
    private Long start;
    @Setter
    private Long end;
    @Setter
    private Boolean reverse;
    @Setter
    private Integer count;
    @Setter
    private Boolean includeTimetoken;

    public History(PubNub pubnub, Retrofit retrofit) {
        super(pubnub, retrofit);
    }

    private interface HistoryService {
        @GET("v2/history/sub-key/{subKey}/channel/{channel}")
        Call<JsonElement> fetchHistory(@Path("subKey") String subKey,
                                    @Path("channel") String channel,
                                    @QueryMap Map<String, String> options);
    }

    @Override
    protected void validateParams() throws PubNubException {
        if (channel == null || channel.isEmpty()) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_CHANNEL_MISSING).build();
        }
    }

    @Override
    protected Call<JsonElement> doWork(Map<String, String> params) {

        HistoryService service = this.getRetrofit().create(HistoryService.class);

        if (reverse != null) {
            params.put("reverse", String.valueOf(reverse));
        }

        if (includeTimetoken != null) {
            params.put("include_token", String.valueOf(includeTimetoken));
        }

        if (count != null && count > 0 && count <= MAX_COUNT) {
            params.put("count", String.valueOf(count));
        } else {
            params.put("count", "100");
        }

        if (start != null) {
            params.put("start", Long.toString(start).toLowerCase());
        }
        if (end != null) {
            params.put("end", Long.toString(end).toLowerCase());
        }

        return service.fetchHistory(this.getPubnub().getConfiguration().getSubscribeKey(), channel, params);
    }

    @Override
    protected PNHistoryResult createResponse(Response<JsonElement> input) throws PubNubException {
        PNHistoryResult.PNHistoryResultBuilder historyData = PNHistoryResult.builder();
        List<PNHistoryItemResult> messages = new ArrayList<>();

        if (input.body() != null) {
            historyData.startTimetoken(input.body().getAsJsonArray().get(1).getAsLong());
            historyData.endTimetoken(input.body().getAsJsonArray().get(2).getAsLong());

            JsonArray historyItems = input.body().getAsJsonArray().get(0).getAsJsonArray();

            for (final JsonElement historyEntry : historyItems) {
                PNHistoryItemResult.PNHistoryItemResultBuilder historyItem = PNHistoryItemResult.builder();
                JsonElement message;

                if (includeTimetoken != null && includeTimetoken) {
                    historyItem.timetoken(historyEntry.getAsJsonObject().get("timetoken").getAsLong());
                    message = processMessage(historyEntry.getAsJsonObject().get("message"));
                } else {
                    message = processMessage(historyEntry);
                }

                historyItem.entry(message);
                messages.add(historyItem.build());
            }

            historyData.messages(messages);
        }

        return historyData.build();
    }

    @Override
    protected PNOperationType getOperationType() {
        return PNOperationType.PNHistoryOperation;
    }

    @Override
    protected boolean isAuthRequired() {
        return true;
    }

    private JsonElement processMessage(JsonElement message) throws PubNubException {
        // if we do not have a crypto key, there is no way to process the node; let's return.
        if (this.getPubnub().getConfiguration().getCipherKey() == null) {
            return message;
        }

        Crypto crypto = new Crypto(this.getPubnub().getConfiguration().getCipherKey());
        String inputText;
        String outputText;
        JsonElement outputObject;

        if (message.isJsonObject() && message.getAsJsonObject().has("pn_other")) {
            inputText = message.getAsJsonObject().get("pn_other").getAsString();
        } else {
            inputText = message.getAsString();
        }

        outputText = crypto.decrypt(inputText);
        outputObject = this.getPubnub().getGsonParser().fromJson(outputText, JsonElement.class);

        // inject the decoded resposne into the payload
        if (message.isJsonObject() && message.getAsJsonObject().has("pn_other")) {
            JsonObject objectNode = message.getAsJsonObject();
            objectNode.add("pn_other", outputObject);
            outputObject = objectNode;
        }

        return outputObject;
    }

}
