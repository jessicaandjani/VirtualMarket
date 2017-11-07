package com.example.appsname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 7/28/2017.
 */
public class OrderLineParser {
    public static String[] ids;
    public static String[] prices;
    public static String[] names;
    public static String[] quantities;
    public static String[] unit_names;
    public static String[] products;

    public static final String orderline_id = "id";
    public static final String price = "price";
    public static final String product_name = "name";
    public static final String quantity = "quantity";
    public static final String unit_name = "unit";

    private JSONArray jsonArray;

    public OrderLineParser(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public void parseJSON(){
        try {
            ids = new String[(jsonArray.length())];
            prices = new String[(jsonArray.length())];
            names = new String[(jsonArray.length())];
            quantities = new String[(jsonArray.length())];
            unit_names = new String[(jsonArray.length())];
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                JSONObject product = jo.getJSONObject("product");//get product
                JSONObject unit = jo.getJSONObject("unit");//get unit
                ids[i] = jo.getString(orderline_id);
                prices[i] = jo.getString(price);
                names[i] = product.getString(product_name);
                quantities[i] = jo.getString(quantity);
                unit_names[i] = unit.getString(unit_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
