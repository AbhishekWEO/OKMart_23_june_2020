package com.okmart.app.model;

public class FeaturedModel {

    private String product_name;
    private String product_ref;
    private String s_price;
    private String current_price;
    private String _seconds;
    private String product_image;
    private String product_status;
    private int quantity;

    private int sold_quantity;
    private boolean isSold;
    private boolean isQuantity;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_ref() {
        return product_ref;
    }

    public void setProduct_ref(String product_ref) {
        this.product_ref = product_ref;
    }

    public String getS_price() {
        return s_price;
    }

    public void setS_price(String s_price) {
        this.s_price = s_price;
    }

    public String getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(String current_price) {
        this.current_price = current_price;
    }

    public String get_seconds() {
        return _seconds;
    }

    public void set_seconds(String _seconds) {
        this._seconds = _seconds;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_status() {
        return product_status;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //

    public int getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(int sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

    public boolean getIsSold() {
        return isSold;
    }

    public void setIsSold(boolean sold) {
        isSold = sold;
    }

    public boolean getIsQuantity() {
        return isQuantity;
    }

    public void setIsQuantity(boolean isQuantity) {
        this.isQuantity = isQuantity;
    }
}