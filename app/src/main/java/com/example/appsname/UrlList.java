package com.example.appsname;

/**
 * Created by asus on 7/17/2017.
 */
public class UrlList {
    private static String IP = "http://167.205.35.43:8001/";

    // Image URL
    public static String CATEGORY_IMG_URL = IP + "api/virtualmarket/images/categories/";
    public static String PRODUCT_IMG_URL = IP + "api/virtualmarket/images/products/";
    // Login & Register
    public static String LOGIN_URL = IP + "api/virtualmarket/user/login";
    public static String REGISTER_URL = IP + "api/virtualmarket/user/register";
    public static String USER_URL = IP + "api/virtualmarket/user/edit";
    public static String USER_PROFILE_URL = IP + "api/virtualmarket/user";
    // Category URL
    public static String CATEGORY_URL = IP + "api/virtualmarket/categories/";
    // Product
    public static String PRODUCT_URL = IP + "api/virtualmarket/product/";
    // Unit & Converter
    public static String COMMON_UNITS_URL = IP + "api/virtualmarket/units/common";
    public static String CONVERTER_URL = IP +  "api/virtualmarket/converter/";
    // Search
    public static String SEARCH_PRODUCT_URL = IP + "api/virtualmarket/product/search/";
    // CheckoutActivity
    public static String SHIPPING_URL = IP + "api/virtualmarket/ratesById/";
    // Order URL
    public static String ORDER_URL = IP + "api/virtualmarket/order/";
    public static String ORDER_ID_URL = IP + "api/virtualmarket/order/id";
    public static String ADD_ORDER_URL = IP + "api/virtualmarket/order/add";
    public static String ADD_ORDERLINE_URL = IP + "api/virtualmarket/orderline/add";
    public static String ORDERLINE_URL = IP + "api/virtualmarket/orderline/";
    // Order Status
    public static String STATUS_URL = IP + "api/virtualmarket/status";
    // Shopping List
    public static String CART_URL = IP + "api/virtualmarket/cart";
    public static String ADD_CART_URL = IP + "api/virtualmarket/cart/add";
    public static String EDIT_CART_URL = IP + "api/virtualmarket/cart/edit";
    public static String EDIT_PRIORITY_URL = IP +  "api/virtualmarket/cart/edit/priority";
    public static String DELETE_CART_URL = IP + "api/virtualmarket/cart/delete";
    public static String REMOVE_CART_URL = IP + "api/virtualmarket/cart/remove";
    // Feedback Activity
    public static String REASON_URL = IP + "api/virtualmarket/reasons";
    public static String ADD_FEEDBACK_URL = IP + "api/virtualmarket/feedback/add";
    public static String ADD_RATING_URL = IP + "api/virtualmarket/rating/add";
    public static String FEEDBACK_HISTORY_URL = IP + "api/virtualmarket/feedback/history/";
}
