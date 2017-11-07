package com.example.appsname;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 4/11/2017.
 */
public class CategoryParser {
    private String imageURL = UrlList.CATEGORY_IMG_URL;
    public static String[] ids;
    public static String[] names;
    public static String[] imgs;

    public static final String json_array = "categories";
    public static final String category_id = "id";
    public static final String category_name = "name";
    public static final String file_img = "category_img";

    private JSONArray categories = null;
    private String json;

    public CategoryParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            categories = jsonObject.getJSONArray(json_array);

            ids = new String[(categories.length())];
            names = new String[(categories.length())];
            imgs = new String[(categories.length())];

            for(int i = 0; i < categories.length(); i++) {
                JSONObject jo = categories.getJSONObject(i);
                ids[i] = jo.getString(category_id);
                names[i] = jo.getString(category_name);
                imgs[i] = imageURL + jo.getString(file_img);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}