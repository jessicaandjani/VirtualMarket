package com.example.appsname;

import android.os.Parcelable;

/**
 * Created by asus on 4/7/2017.
 */
public class OrderStatus  {
    private int statusId;
    private String statusName;
    private String orderStatus;
    private String timeStamp;

    public OrderStatus(){

    }

    public OrderStatus(int statusId, String statusName, String orderStatus, String timeStamp) {
        this.statusId = statusId;
        this.statusName = statusName;
        this.orderStatus = orderStatus;
        this.timeStamp = timeStamp;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int id) {
        statusId = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String name) {
        statusName = name;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String status) {
        orderStatus = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String time) {
        timeStamp = time;
    }
}
