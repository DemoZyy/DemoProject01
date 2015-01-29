package com.pubnub.examples.subscribeAtBoot;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class HelloWorldActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // In new versions of Android, the service may not be activated unless an
        // associated activity is run at least once. This empty activity serves
        // that purpose

        Intent serviceIntent = new Intent(this, PubnubService.class);
        startService(serviceIntent);

        Log.i("HelloWorldActivity", "PubNub Activity Started!");

    }

    public void showNotification(String obj, int notificationNo) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
        Notification notification1 = new Notification(R.drawable.ic_launcher,
                obj, System.currentTimeMillis());

        Intent notificationIntent = new Intent(this, HelloWorldActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification1.setLatestEventInfo(HelloWorldActivity.this, "PUBNUB",
                obj, pendingIntent);
        notificationManager.notify(++notificationNo, notification1);
    }
}