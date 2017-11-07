package com.example.appsname;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 5/30/2017.
 */
public class OrderParser {
    public static int id;
    public static boolean status;
    public static String statusName;
    public static String garendong;
    public static String name;
    public static String timestamp;

    public static final String is_success = "status";
    public static final String status_name = "name";
    public static final String garendong_id = "garendong_id";
    public static final String garendong_name = "garendong";
    public static final String status_id = "order_status";
    public static final String time = "updated_at";

    private String json;

    public OrderParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONObject order = jsonObject.getJSONObject("order");
            JSONObject state = order.getJSONObject("status");
            id = order.getInt(status_id);
            timestamp = order.getString(time);
            garendong = order.getString(garendong_id);
            name = jsonObject.getString(garendong_name);
            status = state.getBoolean(is_success);
            statusName = state.getString(status_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
