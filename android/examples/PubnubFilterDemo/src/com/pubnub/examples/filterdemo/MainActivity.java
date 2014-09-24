package com.pubnub.examples.filterdemo;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Config;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubUtil;
import com.pubnub.examples.filterdemo.R;


public class MainActivity extends Activity {

    Pubnub pubnub;
    SharedPreferences prefs;
    Context context;
    private static final String APP_VERSION = "3.6.1";

    String PUBLISH_KEY = "demo";
    String SUBSCRIBE_KEY = "demo";
    String CIPHER_KEY = "";
    String SECRET_KEY = "demo";
    String ORIGIN = "pubsub";
    String AUTH_KEY;
    String UUID;
    Boolean SSL = false;


    static final String TAG = "Main Activity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final EditText textOrigin = (EditText) findViewById(R.id.textOrigin);
        final EditText textSubscriberTags = (EditText) findViewById(R.id.textSubscriberTags);
        final EditText textChannels = (EditText) findViewById(R.id.textChannels);
        final EditText textPublishTags = (EditText) findViewById(R.id.textPublishTags);
        final EditText textPublishChannel = (EditText) findViewById(R.id.textPublishChannel);
        final EditText textPublishMessage = (EditText) findViewById(R.id.textPublishMessage);
        final EditText textSubscribeOutput = (EditText) findViewById(R.id.textSubscribeOutput);
        
        final Callback subscribeCallback = new Callback(){
        	public void successCallback(String channel, final Object response) {
        		System.out.println(response);
        		runOnUiThread(new Runnable() {
        		     @Override
        		     public void run() {
        		    	String current = textSubscribeOutput.getEditableText().toString();
        		    	if (current.length() > 70) {
        		    		current = "";
        		    	}
        		    	String newString = response.toString() + "\n" + current;
        	        	textSubscribeOutput.setText(newString);

        		    }
        		});

        	}
        };
        
        
        Button buttonPublish = (Button) findViewById(R.id.buttonPublish);
        
        // default values
        textOrigin.setText("registry.devbuild");
        textChannels.setText("demo");
        textSubscriberTags.setText("pubnub");
        textPublishChannel.setText("demo");
        textPublishMessage.setText("Hi");
        textPublishTags.setText("pubnub");
        
        
        
        String origin = textOrigin.getEditableText().toString();
        String channels = textChannels.getEditableText().toString();
        String subtags = textSubscriberTags.getEditableText().toString();
        
        pubnub = new Pubnub("demo", "demo");
        
        pubnub.setCacheBusting(false);
        pubnub.setOrigin(origin);
        pubnub.addFilters(subtags.replace(",  ",",").split(","));
        
        try {
			pubnub.subscribe(channels, subscribeCallback);
		} catch (PubnubException e) {
		}
        
        textOrigin.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			  if(!hasFocus){
				  	if (pubnub != null) {
				  		pubnub.unsubscribeAll();
				  		pubnub.disconnectAndResubscribe();
				  	}
					pubnub = new Pubnub("demo","demo");
			        String origin = textOrigin.getEditableText().toString();
			        String channels = textChannels.getEditableText().toString();
			        
			        
			        pubnub.setCacheBusting(false);
			        pubnub.setOrigin(origin);
			        try {
						pubnub.subscribe(channels, subscribeCallback);
					} catch (PubnubException e) {
					}
			   }
			}
        	
        });
        textSubscriberTags.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				notifyUser(hasFocus);
				System.out.println(hasFocus);
			  if(!hasFocus){

			        String tags = textSubscriberTags.getEditableText().toString();
			        pubnub.removeAllFilters();
			        pubnub.addFilters(tags.replace(",  ",",").split(","));
			        pubnub.disconnectAndResubscribe();
			   }
			}
        	
        });
        textChannels.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			  if(!hasFocus){
				  	pubnub.unsubscribeAll();
			        String channels = textChannels.getEditableText().toString();
			        try {
						pubnub.subscribe(channels, subscribeCallback);
					} catch (PubnubException e) {
					}
			   }
			}
        	
        });
        
        buttonPublish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
		        pubnub = new Pubnub("demo", "demo");
		        JSONObject jso = new JSONObject();
		        pubnub.setCacheBusting(false);
		        String origin = textOrigin.getEditableText().toString();
		        if (!TextUtils.isEmpty(origin)) {
		        	pubnub.setOrigin("registry.devbuild");
		        }

		        String ptags = textPublishTags.getEditableText().toString();
		        String pChannel = textPublishChannel.getEditableText().toString();
		        String pMessage = textPublishMessage.getEditableText().toString();
		        		
		        if (!TextUtils.isEmpty(pChannel)) {
			        if (!TextUtils.isEmpty(ptags) && !TextUtils.isEmpty(pMessage)) {
			        	String[] tags = ptags.replace(",  ", ",").split(",");
			        	JSONArray tagsArray = new JSONArray();
			        	for (String s : tags) {
			        		tagsArray.put(s);
			        	}
			        	try {
							jso.put("tags", tagsArray);
							jso.put("msg", pMessage);
						} catch (JSONException e) {
	
						}
			        	pubnub.publish(pChannel, jso, new Callback(){
			        		public void successCallback(String channel, Object response) {
			        			System.out.println(response);
			        		}
			        		public void errorCallback(String channel, PubnubError error) {
			        			System.out.println(error);
			        		}
			        	});
			        } else if (!TextUtils.isEmpty(pMessage)) {
			        	pubnub.publish(pChannel, pMessage, new Callback(){
			        		public void successCallback(String channel, Object response) {
			        			System.out.println(response);
			        		}
			        		public void errorCallback(String channel, PubnubError error) {
			        			System.out.println(error);
			        		}
			        	});
			        } else {
			        	notifyUser("Message Missing");
			        }
		        } else {
		        	notifyUser("Channel Missing");
		        }
		        
		        
				
			}
        	
        });
        


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	return true;
    }

}
