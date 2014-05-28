package com.pubnub.examples.pubnubsubscribe;

import java.util.ArrayList;

import com.pubnub.examples.pubnubsubscribe.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class PubnubActivity extends Activity {
	
	public static ArrayList<String> list = new ArrayList<String>();
	public static ArrayAdapter adapter;
    BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			PubnubActivity.adapter.notifyDataSetChanged();
			
		}
    	
    };
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        setContentView(R.layout.activity_pubnub);
        
        
        ListView listview = (ListView) findViewById(R.id.message_list);

        adapter = new ArrayAdapter(this,
            android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);
        
        final IntentFilter myFilter = new

        		IntentFilter("android.intent.action.MAIN");

        		registerReceiver(receiver, myFilter);

        
        // In new versions of Android, the service may not be activated unless an
        // associated activity is run at least once. This empty activity serves
        // that purpose

        Intent serviceIntent = new Intent(this, PubnubService.class);
        startService(serviceIntent);

        Log.i("HelloWorldActivity", "PubNub Activity Started!");

    }
}
