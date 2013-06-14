package com.pubnub.examples.BatteryTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Process;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubUtil;
import com.pubnub.examples.BatteryTest.R;



abstract class Suite {
    public abstract void runSuite();
    public abstract void stopSuite();
    private String menuStr;
    Suite(String menuStr) {
        this.menuStr = menuStr;
        BatteryTest.bt.listItems.add(this.menuStr);
    }
}

class PublishThread implements Runnable {
    private int interval;
    private String message;
    private String channel;
    private volatile boolean run = true;

    PublishThread(String channel, String message, int interval) {
        this.interval = interval;
        this.message = message;
        this.channel = channel;
    }

    public void stop() {
        run = false;
    }

    @Override
    public void run() {
        while(run) {
            Pubnub pn = BatteryTest.bt.pubnub;
            pn.publish(channel, message, new Callback() {

                @Override
                public void errorCallback(String arg0, Object arg1) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void successCallback(String arg0, Object arg1) {
                    // TODO Auto-generated method stub

                }});
            try {
                Thread.sleep(this.interval * 1000);
            } catch (InterruptedException e) {
            }
        }

    }
}

class StatsThread implements Runnable {
    private int interval;
    private volatile boolean run = true;
    String  fileName;
    private File sdCard = Environment.getExternalStorageDirectory();
    private File dir = new File(sdCard.getAbsolutePath() + "/battery_app");
    private PrintWriter logFile;
    long datetime;
    private long startAppRxBytes = TrafficStats.getUidRxBytes(Process.myUid());
    private long startAppTxBytes = TrafficStats.getUidTxBytes(Process.myUid());
    private long startTotalRxBytes = TrafficStats.getTotalRxBytes();
    private long startTotalTxBytes = TrafficStats.getTotalTxBytes();

    StatsThread(int interval) {
        this.interval = interval;
        dir.mkdirs();
        try {
            datetime = new Date().getTime();
            String name = "Punub_Battery_App_Report-" + datetime + ".csv";
            fileName = sdCard.getAbsolutePath() + "/battery_app/" + name;
            logFile = new PrintWriter(new FileOutputStream(new File(dir, name)));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        run = false;
        if (logFile != null) logFile.close();

    }

    private void logStats(String stats) {
        if (logFile == null) return;
        logFile.println(stats);
    }
    private String[] getStats() {
        String[] stats = new String[6];
        int i = 0;
        stats[i++] = String.valueOf(TrafficStats.getUidRxBytes(Process.myUid()) - startAppRxBytes);
        stats[i++] = String.valueOf(TrafficStats.getUidTxBytes(Process.myUid()) - startAppTxBytes);
        stats[i++] = String.valueOf(TrafficStats.getTotalRxBytes() - startTotalRxBytes);
        stats[i++] = String.valueOf(TrafficStats.getTotalTxBytes() - startTotalTxBytes);
        stats[i++] = String.valueOf(BatteryTest.bt.batteryStats.getCapacity());
        stats[i++] = String.valueOf(BatteryTest.bt.batteryStats.getCurrent());
        return stats;
    }

    @Override
    public void run() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logStats("Timestamp,AppRxBytes,AppTxBytes,TotalRxBytes,TotalTxBytes,BatteryRemainingInPercentage,BatteryCurrent");
        while(run) {
            try {
                logStats(formatter.format(new Date().getTime()) + "," + PubnubUtil.joinString(getStats(), ","));
                Thread.sleep(this.interval * 1000);
            } catch (InterruptedException e) {
            }
        }

    }

}


class PublishSuite extends Suite {
    int messageSize;
    int interval;
    String channel;
    PublishThread pt;
    PublishSuite(String channel, int messageSize, int interval) {
        super("PUBLISH : " + channel + " : " + messageSize + " bytes  : " + interval + " sec");
        this.channel = channel;
        this.messageSize = messageSize;
        this.interval = interval;
    }
    public void runSuite() {
        char[] arr = new char[messageSize];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = 'a';
        }
        String msg = new String(arr);
        pt = new PublishThread(channel, msg, interval);
        new Thread(pt).start();
    }
    @Override
    public void stopSuite() {
        if (pt != null) pt.stop();
    }
}

class SubscribeSuite extends Suite {
    private String channel;
    SubscribeSuite(String channel) {
        super("SUBSCRIBE : " + channel);
        this.channel = channel;
    }

    @Override
    public void runSuite() {
        try {
            BatteryTest.bt.pubnub.subscribe(new String[]{channel}, new Callback(){
                @Override
                public void successCallback(String channel, Object message) {
                    //Log.d("BatteryTest",message.toString());
                }

                @Override
                public void errorCallback(String arg0, Object arg1) {
                    // TODO Auto-generated method stub

                }
            });
        } catch (PubnubException e) {
        }

    }

    @Override
    public void stopSuite() {
        BatteryTest.bt.pubnub.unsubscribe(channel);

    }

}

class HistorySuite extends Suite {

    HistorySuite(String menuStr) {
        super(menuStr);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void runSuite() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopSuite() {
        // TODO Auto-generated method stub

    }

}


class BatteryStats {
    private double rating;
    private double startCapacity;
    private double endCapacity;

    public double getCapacity() {
        Intent batteryIntent = BatteryTest.bt.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawlevel = batteryIntent.getIntExtra("level", -1);
        double scale = batteryIntent.getIntExtra("scale", -1);
        double level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel / scale;
        }
        return level * 100;
    }

    public double getCurrent() {
        double current = 0;
        try {
            String currentStr = "POWER_SUPPLY_CURRENT_NOW";
            FileReader fr = new FileReader("/sys/class/power_supply/battery/uevent");
            BufferedReader br = new BufferedReader(fr);
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                if (strLine.length() > currentStr.length() &&
                        strLine.substring(0,currentStr.length()).equals(currentStr))
                    current = Double.parseDouble(strLine.split("=")[1]);
            }
            br.close();
        } catch (Exception e) {

        }
        return current;
    }

    public void setStartCapacity() {
        this.startCapacity = getCapacity();
    }
    public double getCapacityConsumed(double startCapacity) {
        this.endCapacity = getCapacity();
        return ( startCapacity - endCapacity) ;
    }

    public double getCapacityConsumed() {
        this.endCapacity = getCapacity();
        return ( startCapacity - endCapacity) ;
    }

    public double getRating() {
        if (rating == 0) {
            try {
                String ratingStr = "POWER_SUPPLY_CHARGE_FULL";
                FileReader fr = new FileReader("/sys/class/power_supply/battery/uevent");
                BufferedReader br = new BufferedReader(fr);
                String strLine;
                while ((strLine = br.readLine()) != null)   {
                    if (strLine.length() > ratingStr.length() &&
                            strLine.substring(0,ratingStr.length()).equals(ratingStr))
                        rating = Double.parseDouble(strLine.split("=")[1]);
                }
                br.close();
            } catch (Exception e) {

            }

        }
        if (rating == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BatteryTest.bt);
            builder.setTitle("Set Battery Rating/Capacity");
            builder.setMessage("Enter Battery Rating/Capcity in mAh");
            final EditText edRating = new EditText(BatteryTest.bt);
            edRating.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(edRating);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    rating = Integer.parseInt(edRating.getText().toString());

                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

        return rating;
    }
}

public class BatteryTest extends Activity {

    public static BatteryTest bt;
    private Handler handler;
    private Runnable runnable;
    private StatsThread statsThread;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    BatteryStats batteryStats = new BatteryStats();


    private double testStartTime = 0;
    private int testDuration = 60;

    List<Suite> testSuites = new ArrayList<Suite>();

    Pubnub pubnub = new Pubnub("demo", "demo", "", false);

    private void notifyUser(Object message) {
        try {
            if (message instanceof JSONObject) {
                final JSONObject obj = (JSONObject) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), obj.toString(),
                                Toast.LENGTH_LONG).show();

                        Log.i("Received msg : ", String.valueOf(obj));
                    }
                });

            } else if (message instanceof String) {
                final String obj = (String) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), obj,
                                Toast.LENGTH_LONG).show();
                        Log.i("Received msg : ", obj.toString());
                    }
                });

            } else if (message instanceof JSONArray) {
                final JSONArray obj = (JSONArray) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), obj.toString(),
                                Toast.LENGTH_LONG).show();
                        Log.i("Received msg : ", obj.toString());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showBatteryUsage() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Result");
        double testDuration = ( (System.currentTimeMillis()/1000) - testStartTime);
        double capacityConsumed = batteryStats.getCapacityConsumed();
        double avgCurrent = (capacityConsumed * batteryStats.getRating()) / (testDuration / 60) ;
        builder.setMessage("Test Stopped. Duration : " +
                + testDuration +
                " sec, Battery Usage :" + capacityConsumed +
                " % , Avg. Current : " +  avgCurrent + " mA" + ", Log File = " + statsThread.fileName);
        final TextView textView = new TextView(this);
        builder.setView(textView);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("Send Report By Email",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("plain/text");
                i.putExtra(Intent.EXTRA_SUBJECT, "Pubnub Battery App Report " + formatter.format(statsThread.datetime));
                i.putExtra(Intent.EXTRA_TEXT   , "Attached is the csv with results of the test");
                i.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("file://" + statsThread.fileName));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(BatteryTest.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bt = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        ListView lvItems = (ListView) findViewById(R.id.listView1);
        lvItems.setAdapter(adapter);
        lvItems.setTextFilterEnabled(true);

        final Button btnClearAll = (Button) findViewById(R.id.btnClearAll);
        final Button btnStopTest = (Button) findViewById(R.id.btnStopTest);
        final Button btnStartTest = (Button) findViewById(R.id.btnStartTest);
        final Button btnMenu = (Button) findViewById(R.id.btnMenu);

        btnClearAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                testSuites.clear();
                listItems.clear();
                adapter.notifyDataSetChanged();
            }});

        btnStartTest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BatteryTest.bt);
                builder.setTitle("Set Test Duration");
                builder.setMessage("Enter Test Duration in Seconds. Default 60 sec");
                batteryStats.getRating();
                final EditText edDuration = new EditText(BatteryTest.bt);
                edDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(edDuration);
                builder.setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            testDuration = Integer.parseInt(edDuration.getText().toString());
                        } catch (Exception e) {
                            testDuration = 60;
                        }
                        testStartTime = System.currentTimeMillis()/1000;
                        statsThread = new StatsThread(60);
                        new Thread(statsThread).start();
                        handler = new Handler();
                        runnable = new Runnable(){

                            @Override
                            public void run() {
                                btnStopTest.performClick();
                            }};

                            handler.postDelayed(runnable, testDuration * 1000);
                            batteryStats.setStartCapacity();
                            synchronized(testSuites) {
                                for (Suite s : testSuites) {
                                    s.runSuite();
                                }
                            }
                            btnStartTest.setEnabled(false);
                            btnStopTest.setEnabled(true);
                            btnClearAll.setEnabled(false);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }});


        btnStopTest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                synchronized(testSuites) {
                    for (Suite s : testSuites) {
                        s.stopSuite();
                    }
                }
                statsThread.stop();
                showBatteryUsage();
                handler.removeCallbacks(runnable);
                btnStartTest.setEnabled(true);
                btnClearAll.setEnabled(true);
                btnStopTest.setEnabled(false);
            }});

        btnStopTest.setEnabled(false);

        btnMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bt.openOptionsMenu();
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

        case R.id.option1:
            subscribe();
            return true;

        case R.id.option2:
            publish();
            return true;

            /*case R.id.option3:
            history();
            return true;


             */
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void history() {
        Intent nextScreen = new Intent(getApplicationContext(), HistoryActivity.class);
        startActivity(nextScreen);

    }

    private void publish() {
        Intent nextScreen = new Intent(getApplicationContext(), PublishActivity.class);
        startActivity(nextScreen);
    }

    private void subscribe() {
        Intent nextScreen = new Intent(getApplicationContext(), SubscribeActivity.class);
        startActivity(nextScreen);

    }

}
