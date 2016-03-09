package com.pubnub.api;

import static com.pubnub.api.PubnubError.PNERR_DECRYPTION_ERROR;
import static com.pubnub.api.PubnubError.PNERR_ENCRYPTION_ERROR;
import static com.pubnub.api.PubnubError.PNERR_FORBIDDEN;
import static com.pubnub.api.PubnubError.PNERR_INVALID_JSON;
import static com.pubnub.api.PubnubError.PNERR_UNAUTHORIZED;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract class to be subclassed by objects being passed as callbacks to
 * Pubnub APIs Default implementation for all methods is blank
 *
 * @author Pubnub
 *
 */


abstract class Callback {
	
    
    ErrorStatus fillErrorStatusDetails(ErrorStatus status, PubnubError error, Result result) {

        status.wasAutoRetried = false;
        status.errorData.information = error.getErrorMessage();
        
        switch(error.errorCode) {
        case PNERR_FORBIDDEN:
        case PNERR_UNAUTHORIZED:
            status.category = StatusCategory.ACCESS_DENIED;
            break;
        case PNERR_ENCRYPTION_ERROR:
        case PNERR_DECRYPTION_ERROR:
            status.category = StatusCategory.ENCRYPTION_ERROR;
            break;
        case PNERR_INVALID_JSON:
            status.category = StatusCategory.NON_JSON_RESPONSE;
            break;
        default:
            break;
            
        }
        return status;
    }
    
    ErrorStatus fillErrorStatusDetails(PubnubError error, Result result) {
        
        
        ErrorStatus status = new ErrorStatus(result);
        status.wasAutoRetried = false;
        status.errorData.information = error.getErrorMessage();
        
        switch(error.errorCode) {
        case PNERR_FORBIDDEN:
        case PNERR_UNAUTHORIZED:
            status.category = StatusCategory.ACCESS_DENIED;
            break;
        case PNERR_ENCRYPTION_ERROR:
        case PNERR_DECRYPTION_ERROR:
            status.category = StatusCategory.ENCRYPTION_ERROR;
            break;
        case PNERR_INVALID_JSON:
            status.category = StatusCategory.NON_JSON_RESPONSE;
            break;
        default:
            break;
            
        }
        return status;
    }
    
	abstract void successCallback(String channel, Object response, Result result);
	abstract void errorCallback(String channel, PubnubError error, Result result);

	
	/*
	public void subscribeCallback(SubscribeResult result) {
		
	}
	
	public void subscribeCallback(SubscribeStatus status) {
		
	}


	void subscribeCallback(StreamStatus status) {

	}
	*/
	/*

	protected void successCallback(String channel, Object message, Result result) {
		if (result == null) {
			successCallback(channel, message);
		} else {

			switch(result.operation) {
			
			case HERE_NOW_FOR_CHANNEL:
			case HERE_NOW_GLOBAL:
			case HERE_NOW_FOR_CHANNEL_GROUP:
				// {"message":"OK","statusInterface":200,"uuids":[],"service":"Presence","occupancy":0}
				JSONObject jso = (JSONObject)message;
				HereNowResult hereNowResult = (HereNowResult)result;
				hereNowResult.type = ResultType.RESULT;
				try {
					hereNowResult.data.occupancy = jso.getInt("occupancy");
					JSONArray uuids = jso.getJSONArray("uuids");
					hereNowResult.data = new HereNowData();
					hereNowResult.data.uuids = new String[uuids.length()];
					if (uuids != null && uuids.length() > 0) {
						for(int i = 0; i < uuids.length(); i++){
						    hereNowResult.data.uuids[i] = uuids.getString(i);
						}
					}


					hereNowCallback(hereNowResult);
				} catch (JSONException e1) {

				}
				
				hereNowCallback(hereNowResult);
				
				break;
				
			case GRANT:
				grantCallback((GrantStatus)result);
				break;
			case PUBLISH:
			    
				JSONArray jsa = (JSONArray)message;
				PublishStatus status = (PublishStatus)result;
				status.type = ResultType.STATUS;
				try {
					if (jsa != null) {
						status.getData().description = jsa.getString(1);
						status.getData().timetoken = jsa.getString(2);
					}
				} catch (JSONException e) {

				}
				publishCallback(status);
				
				break;
				
			case SUBSCRIBE:
			    
				((SubscribeResult)result).data.message = message;

				if (channel.endsWith("-pnpres")) {
	                result.type = ResultType.STATUS;
	                subscribeCallback(new StreamStatus(new StreamResult((SubscribeResult)result)));
				} else {
	                result.type = ResultType.RESULT;
				    subscribeCallback((SubscribeResult)result);
				}
				
				break;
				
			default:
				break;
				
			}
		}
	}
	*/
	
	/*
	void errorCallback(String channel, PubnubError error, Result result) {
		if (result == null) {
			errorCallback(channel, error);
		} else {
			result.type = ResultType.STATUS;
			switch(result.getOperation()) {
			case HERE_NOW_FOR_CHANNEL:
			    
				ErrorStatus hereNowStatus = (ErrorStatus)result;
				hereNowStatus.isError = true;
				hereNowStatus.wasAutoRetried = false;
				switch(error.errorCode) {
				case PubnubError.PNERR_FORBIDDEN:
					hereNowStatus.category = StatusCategory.ACCESS_DENIED;
					break;
				case PubnubError.PNERR_CLIENT_TIMEOUT:
					hereNowStatus.category = StatusCategory.TIMEOUT;
					break;
				}
				hereNowCallback(hereNowStatus);
				hereNowCallback((HereNowResult)hereNowStatus);
				
				break;
			case GRANT:
				//grantCallback((GrantStatus)result);			
				break;
			case PUBLISH:
				
				PublishStatus status = (PublishStatus)result;
				status.status.wasAutoRetried = false;
				status.status.isError = true;
				status.type = ResultType.STATUS;
				
				switch(error.errorCode) {
				case PubnubError.PNERR_FORBIDDEN:
					status.status.category = StatusCategory.ACCESS_DENIED;
					break;
				case PubnubError.PNERR_CLIENT_TIMEOUT:
					status.status.category = StatusCategory.TIMEOUT;
					break;
				}
				//publishCallback(status);
				break;
			default:
				break;
				
			}
		}
	}
	*/
	
    void successWrapperCallback(String channel, Object message, String timetoken, SubscribeResult result) {
        System.out.println("wrapper callback");
        System.out.println(message);
    	result.data.timetoken = timetoken;
        result.data.message = message;
        successCallback(channel, message, result);
    }

    
    void connectCallback(String channel, Object message, SubscribeResult result) {
        System.out.println("CONNECT");
        /*
		SubscribeStatus status = new SubscribeStatus(result);
    	if (status == null) {
    		connectCallback(channel, message);
    	} else {

    		status.type = ResultType.STATUS;
    		status.status.category = StatusCategory.CONNECT;
    		status.status.isError = false;
    		status.status.wasAutoRetried = true;
    		subscribeCallback(status);   		
    		//subscribeCallback((SubscribeResult)status);
    	}
    	*/
    }


    void reconnectCallback(String channel, Object message, SubscribeResult result) {
        /*
		SubscribeStatus status = new SubscribeStatus(result);
    	if (status == null) {
    		reconnectCallback(channel, message);
    	} else {
    		status.status.category = StatusCategory.RECONNECT;
    		status.status.isError = false;
    		status.status.wasAutoRetried = true;
    		subscribeCallback(status);
    		//subscribeCallback((SubscribeResult)status);
    	}
    	*/
    }

    void disconnectCallback(String channel, Object message, SubscribeResult result) {
        /*
		SubscribeStatus status = new SubscribeStatus(result);
    	if (status == null) {
    		disconnectCallback(channel, message);
    	} else {
    		status.status.category = StatusCategory.EXPECTED_DISCONNECT;
    		status.status.isError = false;
    		status.status.wasAutoRetried = true;
    		subscribeCallback(status);
    		//subscribeCallback((SubscribeResult)status);
    	}
    	*/
    }

}

