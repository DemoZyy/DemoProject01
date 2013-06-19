package com.pubnub.examples.BatteryTest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.example.PubnubBatteryUsageTest.R;

public class SubscribeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        Button btnSubscribeAdd = (Button) findViewById(R.id.btnSubscribeAdd);
        btnSubscribeAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String channel = ((EditText)findViewById(R.id.txtSubscribeChannel)).getText().toString();
                if (channel == null || channel.length() == 0)
                    channel = "default-channel";
                SubscribeSuite sbs = new SubscribeSuite(channel);
                BatteryTest.bt.testSuites.add(sbs);
                BatteryTest.bt.adapter.notifyDataSetChanged();
                finish();
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subscribe, menu);
        return true;
    }

}
