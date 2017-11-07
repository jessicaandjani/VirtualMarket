package com.example.appsname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 5/29/2017.
 */
public class OrderStatusParser {
    public static int[] ids;
    public static String[] names;

    public static final String json_array = "statuses";
    public static final String status_id = "id";
    public static final String status_name = "name";

    private JSONArray statuses = null;
    private String json;

    public OrderStatusParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            statuses = jsonObject.getJSONArray(json_array);

            ids = new int[(statuses.length())];
            names = new String[(statuses.length())];

            for(int i = 0; i < statuses.length(); i++) {
                JSONObject jo = statuses.getJSONObject(i);
                ids[i] = jo.getInt(status_id);
                names[i] = jo.getString(status_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
