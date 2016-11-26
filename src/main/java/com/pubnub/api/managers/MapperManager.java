package com.pubnub.api.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.pubnub.api.PubNubException;
import com.pubnub.api.builder.PubNubErrorBuilder;
import lombok.Getter;
import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class MapperManager {

    @Getter
    private Gson objectMapper;
    @Getter
    private Converter.Factory converterFactory;

    public MapperManager() {

        TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
            @Override public void write(JsonWriter out, Boolean value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value);
                }
            }
            @Override public Boolean read(JsonReader in) throws IOException {
                JsonToken peek = in.peek();
                switch (peek) {
                    case BOOLEAN:
                        return in.nextBoolean();
                    //case NULL:
                    //    in.nextNull();
                    //    return null;
                    case NUMBER:
                        return in.nextInt() != 0;
                    case STRING:
                        return Boolean.parseBoolean(in.nextString());
                    default:
                        throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
                }
            }
        };

        this.objectMapper = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                .create();
        this.converterFactory = GsonConverterFactory.create(this.getObjectMapper());
    }

    @SuppressWarnings("unchecked")
    public <T> T fromJson(String input, Class<T> clazz) throws PubNubException {
        try {
            return this.objectMapper.fromJson(input, clazz);
        } catch (JsonParseException e) {
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
        } catch (JsonParseException e) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_JSON_ERROR).errormsg(e.getMessage()).build();
        }
    }


}
