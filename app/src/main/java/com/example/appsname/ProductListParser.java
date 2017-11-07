package com.example.appsname;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 4/15/2017.
 */
public class ProductListParser {
    private String imageURL = UrlList.PRODUCT_IMG_URL;
    public static Integer[] ids;
    public static String[] names;
    public static String[] units;
    public static String[] price;
    public static String[] imgs;

    public static final String json_array = "products";
    public static final String product_id = "id";
    public static final String product_name = "name";
    public static final String product_quantity = "default_quantity";
    public static final String unit_id = "default_unit_id";
    public static final String price_min = "price_min";
    public static final String price_max = "price_max";
    public static final String product_img = "product_img";

    private JSONArray products = null;
    private String json;

    public ProductListParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            products = jsonObject.getJSONArray(json_array);

            ids = new Integer[(products.length())];
            names = new String[(products.length())];
            units = new String[(products.length())];
            price = new String[(products.length())];
            imgs = new String[(products.length())];


            for(int i = 0; i < products.length(); i++) {
                JSONObject jo = products.getJSONObject(i);
                ids[i] = jo.getInt(product_id);
                names[i] = jo.getString(product_name);
                units[i] = jo.getInt(product_quantity) + " " + jo.getString(unit_id);
                price[i] = PreferencesController.formatRupiahRange(jo.getString(price_min), jo.getString(price_max));
                imgs[i] = imageURL + jo.getString(product_img);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
