package com.example.appsname;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 4/17/2017.
 */
public class UnitParser {
    public static String[] ids;
    public static String[] names;
    public static String[] types;

    public static final String json_array = "units";
    public static final String unit_id = "id";
    public static final String unit_name = "unit";
    public static final String unit_type = "unit_type";

    private JSONArray units = null;
    private String json;

    public UnitParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            units = jsonObject.getJSONArray(json_array);

            ids = new String[(units.length())];
            names = new String[(units.length())];
            types = new String[(units.length())];

            for(int i = 0; i < units.length(); i++) {
                JSONObject jo = units.getJSONObject(i);
                ids[i] = jo.getString(unit_id);
                names[i] = jo.getString(unit_name);
                types[i] = jo.getString(unit_type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
