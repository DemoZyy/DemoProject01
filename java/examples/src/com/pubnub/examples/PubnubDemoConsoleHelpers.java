package com.pubnub.examples;

import static com.pubnub.examples.PubnubDemoConsoleHelpers.notifyUser;
import static java.lang.System.out;

import java.util.Scanner;

import org.json.JSONObject;

public class PubnubDemoConsoleHelpers {

    
    static Scanner reader = new Scanner(System.in);;
    
    static void notifyUser(Object message) {
        out.println(message.toString());
    }
    
    static boolean getBooleanFromConsole(String message, boolean optional) {

        int attempt_count = 0;
        String input = null;
        boolean returnVal = false;
        do {
            if (attempt_count > 0)
                notifyUser("Invalid input. ");
            String message1 = message + " ? ( Enter Yes/No or Y/N )";
            message1 = (optional) ? message1 + " ( Optional input. You can skip by pressing enter ) " : message1;
            notifyUser(message1);
            input = reader.nextLine();
            attempt_count++;
        } while ((input == null || input.length() == 0 || (!input.equalsIgnoreCase("yes")
                && !input.equalsIgnoreCase("no") && !input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")))
                && !optional);
        returnVal = (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) ? true : false;
        notifyUser(message + " : " + returnVal);
        return returnVal;
    }
    
    static boolean getBooleanFromConsole(String message) {
        return getBooleanFromConsole(message, false);
    }
    

    static String getStringFromConsole(String message, boolean optional) {

        int attempt_count = 0;
        String input = null;
        do {
            if (attempt_count > 0)
                System.out.print("Invalid input. ");
            String message1 = "Enter " + message;
            message1 = (optional) ? message1 + " ( Optional input. You can skip by pressing enter )" : message1;
            notifyUser(message1);
            input = reader.nextLine();
            attempt_count++;
        } while ((input == null || input.length() == 0) && !optional);
        notifyUser(message + " : " + input);
        return input;
    }

    static JSONObject getJSONObjectFromConsole(String message, boolean optional) {

        int attempt_count = 0;
        String input = null;
        JSONObject input_jso = null;
        do {
            if (attempt_count > 0)
                System.out.print("Invalid input. ");
            String message1 = "Enter " + message;
            message1 = (optional) ? message1 + " ( Optional input. You can skip by pressing enter )" : message1;
            notifyUser(message1);
            input = reader.nextLine();
            try {
                input_jso = new JSONObject(input);
            } catch (Exception e) {
                input_jso = null;
            }
            attempt_count++;
        } while ((input_jso == null || input_jso.length() == 0) && !optional);
        notifyUser(message + " : " + input_jso);
        return input_jso;
    }

    static JSONObject getJSONObjectFromConsole(String message) {
        return getJSONObjectFromConsole(message, false);
    }

    static String getStringFromConsole(String message) {
        return getStringFromConsole(message, false);
    }

    static int getIntFromConsole(String message, boolean optional) {

        int attempt_count = 0;
        String input = null;
        int returnVal = -1;
        do {
            if (attempt_count > 0)
                notifyUser("Invalid input. ");
            String message1 = "Enter " + message;
            message1 = (optional) ? message1 + " ( Optional input. You can skip by pressing enter ) " : message1;
            notifyUser(message1);
            input = reader.nextLine();
            attempt_count++;
            returnVal = Integer.parseInt(input);
        } while ((input == null || input.length() == 0 || returnVal < -1) && !optional);
        notifyUser(message + " : " + returnVal);
        return returnVal;
    }

    static int getIntFromConsole(String message) {
        return getIntFromConsole(message, false);
    }

    
}
