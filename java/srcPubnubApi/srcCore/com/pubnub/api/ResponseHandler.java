package com.pubnub.api;

/**
 * @author PubnubCore
 */

abstract class ResponseHandler {

    public abstract void handleResponse(HttpRequest hreq, String response, Result result);

    public abstract void handleError(HttpRequest hreq, PubnubError error, Result result);

    public void handleTimeout(HttpRequest hreq) {
    }

    public void handleBackFromDar(HttpRequest hreq) {
    }
}
