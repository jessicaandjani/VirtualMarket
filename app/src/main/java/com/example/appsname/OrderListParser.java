package com.example.appsname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 6/19/2017.
 */
public class OrderListParser {
    public static String[] ids;
    public static String[] times;
    public static String[] prices;

    public static final String json_array = "order";
    public static final String order_id = "id";
    public static final String timestamp = "updated_at";
    public static final String total_price = "total_price";

    private JSONArray orders = null;
    private String json;

    public OrderListParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            orders = jsonObject.getJSONArray(json_array);

            ids = new String[(orders.length())];
            times = new String[(orders.length())];
            prices = new String[(orders.length())];


            for(int i = 0; i < orders.length(); i++) {
                JSONObject jo = orders.getJSONObject(i);
                ids[i] = jo.getString(order_id);
                times[i] = jo.getString(timestamp);
                prices[i] = jo.getString(total_price);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
