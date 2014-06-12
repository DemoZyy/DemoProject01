package com.pubnub.examples.pubnubsubscribe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.examples.pubnubsubscribe.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PubnubActivity extends Activity {

	public static final int NOTIFICATION_ID = 1;
	private static final String TAG = "Pubnub";
	private NotificationManager mNotificationManager;
	public ArrayList<String> list = new ArrayList<String>();
	public CustomListAdapter adapter;
	
	public static boolean activityStarted = false; 

	volatile boolean sendNotification = false;

	public static boolean isUrgent(JSONObject message) {
		boolean urgent = false;
		
		try {
			String action = message.getString("action");
			if (action != null && action.equals("apns"))
				urgent = true;
		} catch (JSONException e) {
			
		}
		
		return urgent;
	}
	
	private void sendNotification(String msg, boolean urgent) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, PubnubActivity.class), 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon)
				.setContentTitle("PubNub Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mBuilder.setAutoCancel(true);
	
		if (urgent) {
			mBuilder.setLights(Color.RED, 3000, 3000);
			mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }	);
			WakeLock screenOn = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
			screenOn.acquire();
			screenOn.release();
			try {
				Uri notification = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
						notification);
				r.play();
			} catch (Exception e) {
				Log.i("PUBNUB", e.toString());
			}
		}
		
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	

	}

	public void notifyUser(JSONObject message) {
		Log.i("PUBNUB", "Notify User: " + message.toString());
		String notification = null;
		Object data = null;
		try {
			data = message.get("data");
		} catch (JSONException e1) {
			Log.i("PUBNUB", e1.toString());
			return;
		}
		try {
			notification = message.getString("message");
		} catch (JSONException e) {
			notification = data.toString();
		}

		if (sendNotification) {
			Log.i("PUBNUB", "Sending notification : " + notification);
			sendNotification(notification, isUrgent(message));
		}
		Log.i("PUBNUB", "Add to List " + data.toString());
		list.add(0, data.toString());
		Log.i("PUBNUB", "List Size : " + list.size());
		
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
        		try {
        			adapter.notifyDataSetChanged();
        		} catch (Exception e) {
        			Log.i("PUBNUB", "could not notify on adapter " + e.toString());
        		}
            }
        });

		Log.i("PUBNUB", "Adapter Notified");

	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				notifyUser(new JSONObject(intent.getStringExtra("data")));
			} catch (JSONException e) {
				Log.i("PUBNUB", e.toString());
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pubnub);
		
		moveTaskToBack(true);
		
		activityStarted = true;

		ListView listview = (ListView) findViewById(R.id.message_list);
		/*
		 * adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
		 * list);
		 * 
		 * listview.setAdapter(adapter);
		 */

		adapter = new CustomListAdapter(this, R.layout.custom_list, list);
		listview.setAdapter(adapter);

		IntentFilter myFilter = new IntentFilter("android.intent.action.MAIN");
		registerReceiver(receiver, myFilter);

		Intent serviceIntent = new Intent(this, PubnubService.class);
		startService(serviceIntent);
		
		PubnubService.initPubnub();
		
		PubnubService.pubnub.history(PubnubService.public_channel, 3, true, new Callback() {
			@Override
			public void successCallback(String channel, Object message) {
				Log.i("PUBNUB", message.toString());
				JSONArray jsa;
				try {
					jsa = (JSONArray) ((JSONArray) message).get(0);
					Log.i("PUBNUB", "message count : " + jsa.length());
					for (int i = jsa.length() - 1; i >= 0; i--) {
						notifyUser((JSONObject) jsa.get(i));
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

			}

			@Override
			public void errorCallback(String channel, PubnubError error) {

			}
		});

		Log.i("PubnubActivity", "PubNub Activity Started!");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("PUBNUB", "Messages count : " + list.size());
		adapter.notifyDataSetChanged();
		sendNotification = false;
	}

	@Override
	public void onStop() {
		super.onStop();
		sendNotification = true;
	}
}
