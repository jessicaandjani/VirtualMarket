package com.example.appsname;

/**
 * Created by asus on 4/20/2017.
 */
public class UnitList {
    public String unitId;
    public String unitName;
    public String unitType;

    public UnitList() {

    }

    public String getId() {
        return unitId;
    }

    public UnitList setId(String id) {
        this.unitId = id;
        return this;
    }

    public String getName() {
        return unitName;
    }

    public UnitList setName(String name) {
        this.unitName = name;
        return this;
    }

    public String getType() {
        return unitType;
    }

    public UnitList setType(String type) {
        this.unitType = type;
        return this;
    }
}
