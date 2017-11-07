package com.example.appsname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 7/18/2017.
 */
public class ShoppingListParser {
    private String imageURL = UrlList.PRODUCT_IMG_URL;
    public static String[] ids;
    public static String[] product_ids;
    public static String[] product_names;
    public static String[] quantities;
    public static String[] unit_ids;
    public static String[] unit_names;
    public static String[] unit_types;
    public static String[] prices_min;
    public static String[] prices_max;
    public static String[] is_priorities;
    public static String[] product_imgs;
    public static String subTotalMin, subTotalMax, subTotal;

    public static final String json_array = "shopping_list";
    public static final String cart_id = "id";
    public static final String product_id = "product_id";
    public static final String product_name = "name";
    public static final String product_quantity = "quantity";
    public static final String unit_id = "unit_id";
    public static final String unit_name = "unit";
    public static final String unit_type = "unit_type";
    public static final String price_min = "price_min";
    public static final String price_max = "price_max";
    public static final String product_img = "product_img";
    public static final String is_priority = "is_priority";

    public static JSONArray carts = null;
    private String json;

    public ShoppingListParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            carts = jsonObject.getJSONArray(json_array);
            subTotalMin = jsonObject.getString("subTotalMin"); // get subtotal minimum
            subTotalMax = jsonObject.getString("subTotalMax"); // get subtotal minimum
            subTotal = PreferencesController.formatRupiahRange(subTotalMin, subTotalMax);
            ids = new String[(carts.length())];
            product_ids = new String[(carts.length())];
            product_names = new String[(carts.length())];
            quantities = new String[(carts.length())];
            unit_ids = new String[(carts.length())];
            unit_names = new String[(carts.length())];
            unit_types = new String[(carts.length())];
            prices_min = new String[(carts.length())];
            prices_max = new String[(carts.length())];
            is_priorities = new String[(carts.length())];
            product_imgs = new String[(carts.length())];


            for(int i = 0; i < carts.length(); i++) {
                JSONObject jo = carts.getJSONObject(i);
                JSONObject product = jo.getJSONObject("product");//get product
                JSONObject unit = jo.getJSONObject("unit");//get unit
                ids[i] = jo.getString(cart_id);
                product_ids[i] = jo.getString(product_id);
                product_names[i] = product.getString(product_name);
                quantities[i] = jo.getString(product_quantity);
                unit_ids[i] = jo.getString(unit_id);
                unit_names[i] = unit.getString(unit_name);
                unit_types[i] = unit.getString(unit_type);
                prices_min[i] = jo.getString(price_min);
                prices_max[i] = jo.getString(price_max);
                product_imgs[i] = imageURL + product.getString(product_img);
                is_priorities[i] = jo.getString(is_priority);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
