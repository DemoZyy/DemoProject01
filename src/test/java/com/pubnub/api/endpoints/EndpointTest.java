package com.pubnub.api.endpoints;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.enums.PNOperationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;

public class EndpointTest extends TestHarness {

    PubNub pubnub;


    @Before
    public void beforeEach() throws IOException {
        pubnub = this.createPubNubInstance(8080);
    }

    @Test
    public void testUUID() throws PubNubException {
        Endpoint<Object, Object> endpoint =  new Endpoint<Object, Object>(pubnub, null) {

            @Override
            protected void validateParams() throws PubNubException {
            }

            @Override
            protected Object createResponse(Response input) throws PubNubException {
                return null;
            }

            @Override
            protected PNOperationType getOperationType() {
                return null;
            }

            @Override
            protected boolean isAuthRequired() {
                return true;
            }

            @Override
            protected Call doWork(Map baseParams) throws PubNubException {
                Assert.assertEquals("myUUID", baseParams.get("uuid"));
                return null;
            }
        };

        endpoint.sync();
    }

}
