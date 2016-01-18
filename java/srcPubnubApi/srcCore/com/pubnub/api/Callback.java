package com.pubnub.api;

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
public abstract class Callback {
	
	public void hereNowCallback(HereNowResult result) {
		
	}
	public void hereNowCallback(HereNowStatus status) {
		
	}
	
	public void publishCallback(PublishStatus result) {
		
	}
	
	public void grantCallback(GrantStatus result) {
		
	}

	public void historyCallback(Result result) {
		
	}
	
	public void subscribeCallback(SubscribeResult result) {
		
	}
	
	public void subscribeCallback(SubscribeStatus status) {
		
	}


	void subscribeCallback(StreamStatus status) {

	}

	void successCallback(String channel, Object message, Result result) {
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
				result.type = ResultType.RESULT;
				subscribeCallback((SubscribeResult)result);
				break;
				
			default:
				break;
				
			}
		}
	}
	
	void errorCallback(String channel, PubnubError error, Result result) {
		if (result == null) {
			errorCallback(channel, error);
		} else {
			result.type = ResultType.STATUS;
			switch(result.getOperation()) {
			case HERE_NOW_FOR_CHANNEL:
				HereNowStatus hereNowStatus = (HereNowStatus)result;
				hereNowStatus.status.isError = true;
				hereNowStatus.status.wasAutoRetried = false;
				switch(error.errorCode) {
				case PubnubError.PNERR_FORBIDDEN:
					hereNowStatus.status.category = StatusCategory.ACCESS_DENIED;
					break;
				case PubnubError.PNERR_CLIENT_TIMEOUT:
					hereNowStatus.status.category = StatusCategory.TIMEOUT;
					break;
				}
				hereNowCallback(hereNowStatus);
				hereNowCallback((HereNowResult)hereNowStatus);
				break;
			case GRANT:
				grantCallback((GrantStatus)result);			
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
				publishCallback(status);
				break;
			default:
				break;
				
			}
		}
	}
	
	

    /**
     * This callback will be invoked when a message is received on the channel
     *
     * @param channel
     *            Channel Name
     * @param message
     *            Message
     *
     */
    public void successCallback(String channel, Object message) {
    	
    }

    /**
     * This callback will be invoked when a message is received on the channel
     *
     * @param channel
     *            Channel Name
     * @param message
     *            Message
     * @param timetoken
     *            Timetoken
     */
    public void successCallback(String channel, Object message, String timetoken) {

    }

    void successWrapperCallback(String channel, Object message, String timetoken, SubscribeResult result) {
    	result.data.timetoken = timetoken;
        successCallback(channel, message, result);
        successCallback(channel, message, timetoken);
    }

    /**
     * This callback will be invoked when an error occurs
     *
     * @param channel
     *            Channel Name
     * @param error
     *            error
     */
    public void errorCallback(String channel, PubnubError error) {

    }

    /**
     * This callback will be invoked on getting connected to a channel
     *
     * @param channel
     *            Channel Name
     */
    public void connectCallback(String channel, Object message) {
    }
    
    void connectCallback(String channel, Object message, SubscribeResult result) {
		SubscribeStatus status = new SubscribeStatus(result);
    	if (status == null) {
    		connectCallback(channel, message);
    	} else {

    		status.type = ResultType.STATUS;
    		status.status.category = StatusCategory.CONNECT;
    		status.status.isError = false;
    		status.status.wasAutoRetried = true;
    		subscribeCallback(status);   		
    		subscribeCallback((SubscribeResult)status);
    	}
    }

    /**
     * This callback is invoked on getting reconnected to a channel after
     * getting disconnected
     *
     * @param channel
     *            Channel Name
     */
    public void reconnectCallback(String channel, Object message) {
    }

    public void reconnectCallback(String channel, Object message, SubscribeResult result) {
		SubscribeStatus status = new SubscribeStatus(result);
    	if (status == null) {
    		reconnectCallback(channel, message);
    	} else {
    		status.status.category = StatusCategory.RECONNECT;
    		status.status.isError = false;
    		status.status.wasAutoRetried = true;
    		subscribeCallback(status);
    		subscribeCallback((SubscribeResult)status);
    	}
    }
    
    /**
     * This callback is invoked on getting disconnected from a channel
     *
     * @param channel
     *            Channel Name
     */
    public void disconnectCallback(String channel, Object message) {
    }
    
    void disconnectCallback(String channel, Object message, SubscribeResult result) {
		SubscribeStatus status = new SubscribeStatus(result);
    	if (status == null) {
    		disconnectCallback(channel, message);
    	} else {
    		status.status.category = StatusCategory.EXPECTED_DISCONNECT;
    		status.status.isError = false;
    		status.status.wasAutoRetried = true;
    		subscribeCallback(status);
    		subscribeCallback((SubscribeResult)status);
    	}
    }

}
