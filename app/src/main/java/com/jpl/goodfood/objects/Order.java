package com.jpl.goodfood.objects;

/**
 * Created by chethan on 03/04/15.
 */
public class Order {
    String objectId;
    String menuItem;
    int orderQty;
    String location;
    String phoneNumber;

    public Order(String objectId, String menuItem, int orderQty, String location) {
        this.menuItem = menuItem;
        this.orderQty = orderQty;
        this.location = location;
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(String menuItem) {
        this.menuItem = menuItem;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
