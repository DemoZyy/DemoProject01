package com.pubnub.api.managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.pubnub.api.PubNubException;
import com.pubnub.api.builder.PubNubErrorBuilder;
import lombok.Getter;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapperManager {

    @Getter
    private Gson objectMapper;
    @Getter
    private Converter.Factory converterFactory;

    public MapperManager() {
        this.objectMapper = new Gson();
        this.converterFactory = GsonConverterFactory.create(this.getObjectMapper());
    }

    @SuppressWarnings("unchecked")
    public <T> T fromJson(String input, Class<T> clazz) throws PubNubException {
        try {
            return this.objectMapper.fromJson(input, clazz);
        } catch (JsonSyntaxException e) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_PARSING_ERROR).errormsg(e.getMessage()).build();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T convertValue(JsonElement input, Class clazz) {
        return (T) this.objectMapper.fromJson(input, clazz);
    }

    public String toJson(Object input) throws PubNubException {
        try {
            return this.objectMapper.toJson(input);
        } catch (JsonSyntaxException e) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_JSON_ERROR).errormsg(e.getMessage()).build();
        }
    }


}
