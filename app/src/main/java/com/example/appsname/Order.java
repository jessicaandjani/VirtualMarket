package com.example.appsname;

import java.util.Date;

/**
 * Created by asus on 6/19/2017.
 */
public class Order {
    private String orderId;
    private String orderTime;

    public Order(){

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String id) {
        orderId = id;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String time) {
        orderTime = time;
    }
}
