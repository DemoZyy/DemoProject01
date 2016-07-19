package com.pubnub.api.managers;


import com.pubnub.api.PubNub;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.vendor.AppEngineFactory;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitManager {

    private PubNub pubnub;

    @Getter
    private Retrofit transactionInstance;
    @Getter
    private Retrofit subscriptionInstance;

    public RetrofitManager(PubNub pubNubInstance) {
        this.pubnub = pubNubInstance;

        this.transactionInstance = createRetrofit(
                this.pubnub.getConfiguration().getNonSubscribeRequestTimeout(),
                this.pubnub.getConfiguration().getConnectTimeout()
        );

        this.subscriptionInstance = createRetrofit(
                this.pubnub.getConfiguration().getSubscribeTimeout(),
                this.pubnub.getConfiguration().getConnectTimeout()
        );

    }

    protected final Retrofit createRetrofit(int requestTimeout, int connectTimeOut) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        if (pubnub.getConfiguration().isGoogleAppEngineNetworking()) {
            retrofitBuilder.callFactory(new AppEngineFactory.Factory());
        } else {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(requestTimeout, TimeUnit.SECONDS);
            httpClient.connectTimeout(connectTimeOut, TimeUnit.SECONDS);
            retrofitBuilder.client(httpClient.build());

            if (pubnub.getConfiguration().getLogVerbosity() == PNLogVerbosity.BODY) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }

        }

        return retrofitBuilder
                .baseUrl(pubnub.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

}
