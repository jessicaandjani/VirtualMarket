package com.example.appsname;

/**
 * Created by asus on 6/16/2017.
 */
public class Reason {
    public String reasonId;
    public String reason;
    public boolean selected = false;


    public Reason() {

    }

    public String getId() {
        return reasonId;
    }

    public Reason setId(String id) {
        this.reasonId = id;
        return this;
    }

    public String getName() {
        return reason;
    }

    public Reason setName(String reason) {
        this.reason = reason;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public Reason setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
}
