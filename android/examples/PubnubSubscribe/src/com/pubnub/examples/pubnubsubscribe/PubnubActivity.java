package com.pubnub.examples.pubnubsubscribe;

import java.util.ArrayList;

import com.pubnub.examples.pubnubsubscribe.R;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class PubnubActivity extends Activity {
	
    public static final int NOTIFICATION_ID = 1;
	private static final String TAG = "PubnubGcm";
    private NotificationManager mNotificationManager;
	public ArrayList<String> list = new ArrayList<String>();
	public ArrayAdapter adapter;
	
	boolean sendNotification = true;
	
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PubnubActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.icon)
        .setContentTitle("GCM Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

	
	public void notifyUser(String message) {
		if (!sendNotification) {
			list.add(0, message);
			adapter.notifyDataSetChanged();
		} else {
			sendNotification(message);
		}
	}
	
    BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			notifyUser(intent.getStringExtra("data"));
		}
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_pubnub);
        
        ListView listview = (ListView) findViewById(R.id.message_list);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);
        
        IntentFilter myFilter = new IntentFilter("android.intent.action.MAIN");
        registerReceiver(receiver, myFilter);

        Intent serviceIntent = new Intent(this, PubnubService.class);
        startService(serviceIntent);

        Log.i("HelloWorldActivity", "PubNub Activity Started!");

    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	sendNotification = false;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	sendNotification = true;
    }
}
