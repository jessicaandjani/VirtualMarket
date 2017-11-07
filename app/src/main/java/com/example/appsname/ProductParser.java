package com.example.appsname;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 4/15/2017.
 */
public class ProductParser {
    private String imageURL = UrlList.PRODUCT_IMG_URL;
    public static String id;
    public static String name;
    public static int quantity;
    public static String unit;
    public static String unitProduct;
    public static String price;
    public static int priceMin;
    public static int priceMax;
    public static String img;
    public static String unitType;
    public static String unitID;

    public static final String product_id = "id";
    public static final String product_name = "name";
    public static final String product_quantity = "default_quantity";
    public static final String unit_id = "default_unit_id";
    public static final String price_min = "price_min";
    public static final String price_max = "price_max";
    public static final String product_img = "product_img";
    public static final String unit_type = "unit_type";
    public static final String unitId = "id";

    private String json;

    public ProductParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONObject product = jsonObject.getJSONObject("detail");
            JSONObject unitJSON = product.getJSONObject("unit");
            id = product.getString(product_id);
            name = product.getString(product_name);
            quantity = product.getInt(product_quantity);
            unit = product.getString(unit_id);
            unitProduct = quantity  + " " + product.getString(unit_id);
            priceMin = product.getInt(price_min);
            priceMax = product.getInt(price_max);
            price = PreferencesController.formatRupiahRange(String.valueOf(priceMin), String.valueOf(priceMax));
            img = imageURL + product.getString(product_img);
            unitType = unitJSON.getString(unit_type);
            unitID = unitJSON.getString(unitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
