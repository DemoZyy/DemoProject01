package com.pubnub.examples.pubnubsubscribe;


import org.json.JSONArray;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class PubnubService extends Service {

	private static final String TAG = "PubnubGcm";

    public static final String ACTION_FROM_SERVICE = null;
	
    String device_channel = "Android-1";
	String public_channel = "public";
	String auth_key = "Android-AuthToken";
	String uuid = "Android-user1";
	
	String publish_key = "pam";
	String subscribe_key = "pam";
	
    Pubnub pubnub;
    PowerManager.WakeLock wl = null;

    private void notifyUser(Object message) {
    	Intent intent = new Intent("android.intent.action.MAIN");
        intent.putExtra("data", message.toString());
        sendBroadcast(intent);
    }

    public void onCreate() {
        super.onCreate();
        pubnub = new Pubnub(publish_key, subscribe_key, false);
        pubnub.setUUID(uuid);
        pubnub.setAuthKey(auth_key);
        
        pubnub.history(public_channel, 3, new Callback(){
        	@Override
        	public void successCallback(String channel, Object message) {
        		Log.i("PUBNUB", message.toString());
        		JSONArray jsa ;
				try {
					jsa = (JSONArray) ((JSONArray) message).get(0);
	        		 for (int i=0; i < 3; i++) {
	 					notifyUser(jsa.get(0));
	         		}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		

        		
        	}
        	@Override
        	public void errorCallback(String channel, PubnubError error) {
        		
        	}
        });
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (wl != null) {
            wl.acquire();
            Log.i("PUBNUB", "Partial Wake Lock : " + wl.isHeld());
        }

        Log.i("PUBNUB", "PubnubService created...");
        
        
        
        try {
            pubnub.subscribe(public_channel, new Callback() {
                public void connectCallback(String channel) {
                    
                	try {
						pubnub.subscribe(device_channel, new Callback() {
			                public void connectCallback(String channel) {
			                    //notifyUser("CONNECT on channel:" + channel);
			                }
			                public void disconnectCallback(String channel) {
			                    //notifyUser("DISCONNECT on channel:" + channel);
			                }
			                public void reconnectCallback(String channel) {
			                    //notifyUser("RECONNECT on channel:" + channel);
			                }
			                @Override
			                public void successCallback(String channel, Object message) {
			                    notifyUser(message.toString());
			                }
			                @Override
			                public void errorCallback(String channel, Object message) {
			                    notifyUser(message.toString());
			                }
						});
					} catch (PubnubException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
                }
                public void disconnectCallback(String channel) {
                    //notifyUser("DISCONNECT on channel:" + channel);
                }
                public void reconnectCallback(String channel) {
                    //notifyUser("RECONNECT on channel:" + channel);
                }
                @Override
                public void successCallback(String channel, Object message) {
                    notifyUser(message.toString());
                }
                @Override
                public void errorCallback(String channel, Object message) {
                    notifyUser(message.toString());
                }
            });
        } catch (PubnubException e) {

        }
           
    }
    


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wl != null) {
            wl.release();
            Log.i("PUBNUB", "Partial Wake Lock : " + wl.isHeld());
            Toast.makeText(this, "Partial Wake Lock : " + wl.isHeld(), Toast.LENGTH_LONG).show();
            wl = null;
        }
        Toast.makeText(this, "PubnubService destroyed...", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}