package com.pubnub.examples.BatteryTest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PublishActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);
        Button btnPublishAdd = (Button) findViewById(R.id.btnPublishAdd);
        btnPublishAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String channel = ((EditText)findViewById(R.id.txtPublishChannel)).getText().toString();
                if (channel == null || channel.length() == 0)
                    channel = "default-channel";
                PublishSuite pbs = new PublishSuite(
                        channel,
                        Integer.parseInt(((Spinner)findViewById(R.id.spinnerPublishMsgSize)).getSelectedItem().toString()),
                        Integer.parseInt(((Spinner)findViewById(R.id.spinnerPublishMsgInterval)).getSelectedItem().toString())
                        );
                BatteryTest.bt.testSuites.add(pbs);
                BatteryTest.bt.adapter.notifyDataSetChanged();
                finish();
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.publish, menu);
        return true;
    }

}
