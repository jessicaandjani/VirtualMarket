package com.example.appsname;

/**
 * Created by asus on 3/28/2017.
 */
public class ShoppingList {
    public String cartId;
    public String productImage;
    public String productId;
    public String productName;
    public String quantity;
    public String unitId;
    public String unit;
    public String unitType;
    public String price_min;
    public String price_max;
    public String isPriority;
    public String subTotal;


    public ShoppingList() {

    }

    public String getID() {
        return cartId;
    }

    public ShoppingList setID(String id) {
        this.cartId = id;
        return this;
    }

    public String getImage() {
        return productImage;
    }

    public ShoppingList setImage(String img) {
        this.productImage = img;
        return this;
    }

    public String getName() {
        return productName;
    }

    public ShoppingList setName(String name) {
        this.productName = name;
        return this;
    }

    public String getPoductId() {
        return productId;
    }

    public ShoppingList setProductId(String id) {
        this.productId = id;
        return this;
    }

    public String getQuantity() {
        return quantity;
    }

    public ShoppingList setQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getUnitId() {
        return unitId;
    }

    public ShoppingList setUnitId(String id) {
        this.unitId = id;
        return this;
    }

    public String getUnitType() {
        return unitType;
    }

    public ShoppingList setUnitType(String type) {
        this.unitType = type;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public ShoppingList setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public String getPriceMin() {
        return price_min;
    }

    public ShoppingList setPriceMin(String price_min) {
        this.price_min = price_min;
        return this;
    }

    public String getPriceMax() {
        return price_max;
    }

    public ShoppingList setPriceMax(String price_max) {
        this.price_max = price_max;
        return this;
    }

    public String getIsPriority() {
        return isPriority;
    }

    public ShoppingList setPriority(String isPriority) {
        this.isPriority = isPriority;
        return this;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public ShoppingList setSubtotal(String subTotal) {
        this.subTotal = subTotal;
        return this;
    }
}

