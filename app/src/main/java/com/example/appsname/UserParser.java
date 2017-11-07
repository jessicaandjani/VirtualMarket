package com.example.appsname;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 7/10/2017.
 */
public class UserParser {
    public static String name;
    public static String username;
    public static String address;
    public static String addressNote;
    public static String phone_number;

    public static final String user_name = "name";
    public static final String user_username = "username";
    public static final String user_address = "address";
    public static final String user_address_note = "address_note";
    public static final String user_phone = "phone_number";

    private String json;

    public UserParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONObject user = jsonObject.getJSONObject("user");
            name = user.getString(user_name);
            username = user.getString(user_username);
            address = user.getString(user_address);
            addressNote = user.getString(user_address_note);
            phone_number = user.getString(user_phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
