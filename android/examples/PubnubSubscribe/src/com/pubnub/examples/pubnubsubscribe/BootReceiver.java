package com.pubnub.examples.pubnubsubscribe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.i("PUBNUB", "PubNub BootReceiver Starting");
        Intent intent = new Intent(context, PubnubService.class);
        context.startService(intent);
        Log.i("PUBNUB", "PubNub BootReceiver Started");
        
    }

}
