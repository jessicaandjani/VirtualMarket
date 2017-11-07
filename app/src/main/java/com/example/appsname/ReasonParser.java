package com.example.appsname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 5/29/2017.
 */
public class ReasonParser {
    public static String[] ids;
    public static String[] names;

    public static final String json_array = "reasons";
    public static final String reason_id = "id";
    public static final String reason_name = "reason";

    private JSONArray reasons = null;
    private String json;

    public ReasonParser(String json) {
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            reasons = jsonObject.getJSONArray(json_array);

            ids = new String[(reasons.length())];
            names = new String[(reasons.length())];

            for(int i = 0; i < reasons.length(); i++) {
                JSONObject jo = reasons.getJSONObject(i);
                ids[i] = jo.getString(reason_id);
                names[i] = jo.getString(reason_name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
