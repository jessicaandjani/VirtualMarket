package com.example.appsname;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by asus on 7/10/2017.
 */
public class PreferencesController {
    private static  final String ORDER = "order";
    private static  final String ORDER_ID = "order_id";
    private static  final String ID = "user_id";

    public static void setUser(Context context, String id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ShoppingList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ID, id);
        editor.apply();
    }

    public static String getID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ShoppingList", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString(ID, null);
        return userID;
    }

    public static void removeUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ShoppingList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ID);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        if(getID(context) != null) {
            return true;
        } else {
            return false;
        }
    }

    public static String formatRupiah(String number1) {
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String price = rupiahFormat.format(Double.parseDouble(number1));
        String result = "Rp " + " " + price;
        return result;
    }

    public static String formatRupiahRange(String number1, String number2) {
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String priceMin = rupiahFormat.format(Double.parseDouble(number1));
        String priceMax = rupiahFormat.format(Double.parseDouble(number2));
        String result = "Rp " + " " + priceMin + " - " + priceMax;
        return result;
    }
}
