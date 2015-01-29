package com.pubnub.examples.subscribeAtBoot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

public class PubnubService extends Service {

    String channel = "hello_world123";
    Pubnub pubnub = new Pubnub("demo", "demo", false);
    PowerManager.WakeLock wl = null;
    static int notificationNo = 1;

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String pnMsg = msg.obj.toString();

            final Toast toast = Toast.makeText(getApplicationContext(), pnMsg, Toast.LENGTH_LONG);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 10000);

        }
    };

    private void notifyUser(Object message) {

        Message msg = handler.obtainMessage();

        try {
            final String obj = (String) message;
            msg.obj = obj;
            handler.sendMessage(msg);
            Log.i("Received msg : ", obj.toString());

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), notification);
            mediaPlayer.start();

            NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
            Notification notif = new Notification();
            notif.ledARGB = 0xFFff0000;
            notif.flags = Notification.FLAG_SHOW_LIGHTS;
            notif.ledOnMS = 100;
            notif.ledOffMS = 100;
            nm.notify(1, notif);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {
        super.onCreate();
        pubnub.setMaxRetries(1000000000);
        Toast.makeText(this, "PubnubService created...", Toast.LENGTH_LONG).show();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (wl != null) {
            wl.acquire();
            Log.i("PUBNUB", "Partial Wake Lock : " + wl.isHeld());
            Toast.makeText(this, "Partial Wake Lock : " + wl.isHeld(), Toast.LENGTH_LONG).show();
        }

        Log.i("PUBNUB", "PubnubService created...");
        try {
            pubnub.subscribe(new String[] {channel}, new Callback() {
                public void connectCallback(String channel) {
                    wl.acquire();
                    notifyUser("CONNECT on channel:" + channel);
                    wl.release();
                }
                public void disconnectCallback(String channel) {
                    wl.acquire();
                    notifyUser("DISCONNECT on channel:" + channel);
                    wl.release();
                }
                public void reconnectCallback(String channel) {
                    notifyUser("RECONNECT on channel:" + channel);
                }
                @Override
                public void successCallback(String channel, Object message) {
                    wl.acquire();
                    notifyUser(channel + " " + message.toString());
                    wl.release();
                }
                @Override
                public void errorCallback(String channel, Object message) {
                    wl.acquire();
                    notifyUser(channel + " " + message.toString());
                    wl.release();
                }
            });
        } catch (PubnubException e) {

        }
        if (wl != null) {
            wl.release();
            Log.i("PUBNUB", "Partial Wake Lock : " + wl.isHeld());
            Toast.makeText(this, "Partial Wake Lock : " + wl.isHeld(), Toast.LENGTH_LONG).show();
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
