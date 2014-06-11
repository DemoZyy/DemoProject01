package com.pubnub.examples;

import static java.lang.System.out;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

class PrintCallback extends Callback {

	@Override
	public void successCallback(String channel, Object response) {
		System.out.println(response);
	}

	@Override
	public void errorCallback(String channel, PubnubError error) {
		System.out.println(error);
	}
}

class TokenRequestProcessor {

	private String uuid = "Java-server1";

	private String iosAuthToken = "iOS-authToken";
	private String androidAuthToken = "Android-AuthToken";
	private String serverAuthToken = "serverAuthToken";

	private String iosChannel = "iOS-1";
	private String androidChannel = "Android-1";
	private String publicChannel = "public";
	Scanner reader = new Scanner(System.in);
	Pubnub pubnub;
	Random random = new Random();

	String[] phrases = new String[] {
			"In non-defaulted market segments, plan to collateralize OTC forward rate agreements.",
			"Lower-classed reference entities: in the technology exchange, never diversify them.",
			"The smart trader nowadays will be sure to hedge unsecured loan instruments.",
			"The smart investor this season will be sure not to underwrite revolving liens.",
			"In high-maturity sectors, always insure revenue-neutral currencies.",
			"Pro rata debts: in the commodities marketplace, be sure not to securitize them." };

	void provisionReadOnlyPAMPermissions(String channel, final String authKey, final Callback callback) {
		pubnub.pamGrant(channel, authKey, true, false, new Callback(){
			@Override
			public void errorCallback(String channel, PubnubError error) {
				System.out.println(error);
			}
			@Override
			public void successCallback(String channel, Object response) {
				pubnub.pamGrant(channel + "-pnpres", authKey, true, true, callback);
			}
		});
		
	}

	void provisionReadWritePAMPermissions(String channel, final String authKey, final Callback callback) {
		pubnub.pamGrant(channel, authKey, true, true, new Callback(){
			@Override
			public void errorCallback(String channel, PubnubError error) {
				System.out.println(error);
			}
			@Override
			public void successCallback(String channel, Object response) {
				System.out.println(response);
				pubnub.pamGrant(channel + "-pnpres", authKey, true, true, callback);
			}
		});
	}

	private void notifyUser(Object message) {
		out.println(message.toString());
	}

	private String getStringFromConsole(String message, boolean optional) {

		int attempt_count = 0;
		String input = null;
		do {
			if (attempt_count > 0)
				System.out.print("Invalid input. ");
			String message1 = "Enter " + message;
			message1 = (optional) ? message1
					+ " ( Optional input. You can skip by pressing enter )"
					: message1;
			notifyUser(message1);
			input = reader.nextLine();
			attempt_count++;
		} while ((input == null || input.length() == 0) && !optional);
		notifyUser(message + " : " + input);
		return input;
	}

	private void displayMenuOptions() {
		System.out.println("(p)ublish a custom message");
		System.out.println("publish a (r)andom message on public channel");
	}

	public void setupPermissions() {
		pubnub = new Pubnub("pam", "pam", "pam", true);
		pubnub.setAuthKey(serverAuthToken);

		// Provision PAM Permissions on Startup
		// First, for devices
		System.out.println("Setting up channel permissions .... ");
		provisionReadOnlyPAMPermissions(androidChannel, androidAuthToken, new Callback(){
			@Override
			public void errorCallback(String channel, PubnubError error) {
				System.out.println(error);
			}
			@Override
			public void successCallback(String channel, Object response) {
				System.out.println(response);
				provisionReadOnlyPAMPermissions(iosChannel, iosAuthToken, new Callback(){
					@Override
					public void errorCallback(String channel, PubnubError error) {
						System.out.println(error);
					}
					@Override
					public void successCallback(String channel, Object response) {
						System.out.println(response);
						provisionReadWritePAMPermissions(androidChannel, serverAuthToken, new Callback() {
							@Override
							public void errorCallback(String channel, PubnubError error) {
								System.out.println(error);
							}
							@Override
							public void successCallback(String channel, Object response) {
								System.out.println(response);
								provisionReadWritePAMPermissions(iosChannel, serverAuthToken, new Callback(){
									@Override
									public void errorCallback(String channel, PubnubError error) {
										System.out.println(error);
									}
									@Override
									public void successCallback(String channel, Object response) {
										System.out.println(response);
										try {
											pubnub.presence(new String[] { publicChannel, iosChannel,
													androidChannel }, new Callback() {
												@Override
												public void errorCallback(String channel, PubnubError error) {
													System.out.println(error);
												}
												public void successCallback(String channel, Object response) {
												}
											});
										} catch (PubnubException e) {
											e.printStackTrace();
										}
										System.out.println("Done");
										new Thread(new Runnable(){
											public void run() {
												start();
											}
										}).start();
									}
								});
									
							}
							
						});
					}
				});
			}
		});
		
	}
	
	public void start() {

		String command = "";
		displayMenuOptions();
		while ((command = reader.next()) != "q") {
			reader.nextLine();
			if (command.equalsIgnoreCase("p")) {
				String channel = getStringFromConsole("Channel Name",
						false);
				notifyUser("Enter the message for publish");
				String userMessage = "";

				userMessage = reader.nextLine();
				JSONObject message = new JSONObject();
				try {
					if (command.equals("r")) {
						message.put("action", "apns");
					}
                    message.put("message", "New Message Available");
					message.put("timestamp", new SimpleDateFormat(
							"MM/dd/yy HH:mma").format(new Date()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				boolean parsed = false;
				if (!parsed) {
					try {
						Integer i = Integer.parseInt(userMessage);
						message.put("data", i);
						parsed = true;
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
				if (!parsed) {
					try {
						Double d = Double.parseDouble(userMessage);
						message.put("data", d);
						parsed = true;
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
				if (!parsed) {
					try {
						JSONArray js = new JSONArray(userMessage);
						message.put("data", js);
						parsed = true;
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
				if (!parsed) {
					try {
						JSONObject js = new JSONObject(userMessage);
						message.put("data", js);
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
				if (!parsed) {
					try {
						message.put("data", userMessage);
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}

				pubnub.publish(channel, message, new Callback(){
					public void successCallback(String channel, Object response) {
						System.out.println(response);
						displayMenuOptions();
					}
					public void errorCallback(String channel, PubnubError error) {
						System.out.println(error);
						displayMenuOptions();
					}
				});
			}
			if (command.equalsIgnoreCase("r")) {
				JSONObject message = new JSONObject();
				try {
					if (command.equals("r")) {
						message.put("action", "apns");
					}
                    message.put("message", "New Message Available");
					message.put("timestamp", new SimpleDateFormat(
							"MM/dd/yy HH:mma").format(new Date()));
					int index = Math.abs(random.nextInt())
							% phrases.length;
					message.put("data", phrases[index]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pubnub.publish(publicChannel, message, new Callback(){
					public void successCallback(String channel, Object response) {
						System.out.println(response);
						displayMenuOptions();
					}
					public void errorCallback(String channel, PubnubError error) {
						System.out.println(error);
						displayMenuOptions();
					}
				});
			}

		}
	}
}

public class PubnubTokenRequest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TokenRequestProcessor().setupPermissions();
	}

}
