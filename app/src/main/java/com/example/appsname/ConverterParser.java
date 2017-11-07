package com.example.appsname;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 4/17/2017.
 */
public class ConverterParser {
    public static String ids;
    public static String units;
    public static int in_gram;

    public static final String json_array = "converter";
    public static final String converter_id = "id";
    public static final String unit_id = "unit_id";
    public static final String gram = "in_gram";

    private String json;

    public ConverterParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONObject converter = jsonObject.getJSONObject(json_array);
            ids = converter.getString(converter_id);
            units = converter.getString(unit_id);
            in_gram = converter.getInt(gram);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
