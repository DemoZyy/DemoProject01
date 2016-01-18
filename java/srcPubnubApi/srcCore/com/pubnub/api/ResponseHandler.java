package com.pubnub.api;

/**
 * @author PubnubCore
 */

abstract class ResponseHandler {

    public void handleResponse(HttpRequest hreq, String response, Result result) {
        if (result == null) {
            handleResponse(hreq, response);
        }
    }

    public void handleError(HttpRequest hreq, PubnubError error, Result result){
        if (result == null) {
            handleError(hreq, error);
        }
    }

    public void handleResponse(HttpRequest hreq, String response) {

    }

    public void handleError(HttpRequest hreq, PubnubError error){

    }

    public void handleTimeout(HttpRequest hreq) {
    }

    public void handleBackFromDar(HttpRequest hreq) {
    }
}
