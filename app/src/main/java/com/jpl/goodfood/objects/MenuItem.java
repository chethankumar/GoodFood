package com.jpl.goodfood.objects;

/**
 * Created by chethan on 03/04/15.
 */
public class MenuItem {
    String menuName;
    String menuDetail;
    int price;
    String imageSrc;

    public MenuItem(String menuName, String menuDetail, int price, String imageSrc) {
        this.menuName = menuName;
        this.menuDetail = menuDetail;
        this.price = price;
        this.imageSrc = imageSrc;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(String menuDetail) {
        this.menuDetail = menuDetail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}
