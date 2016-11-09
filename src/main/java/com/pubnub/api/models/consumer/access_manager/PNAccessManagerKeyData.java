package com.pubnub.api.models.consumer.access_manager;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PNAccessManagerKeyData {

    @SerializedName("r")
    private boolean readEnabled;

    @SerializedName("w")
    private boolean writeEnabled;

    @SerializedName("m")
    private boolean manageEnabled;

}
