package com.pubnub.api.models.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pubnub.api.models.mappers.PNJsonEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryForChannelsItem {

    @JsonProperty("message")
    @Getter private PNJsonEntity message;

    @JsonProperty("timetoken")
    @Getter private Long timeToken;

}
