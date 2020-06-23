package com.okmart.app.model;

public class FlashModel {

    private String product_name;
    private String product_ref;
    private String s_price;
    private String current_price;
    private String _seconds;
    private String product_image;
    private String features, product_status;
    private int product_status_number;
    private int quantity;

    private String flash_down_second;
    private String down_per_price;
    private String min_price;
    private String flash_sale_order_time;
    private String current_time;

    private int sold_quantity;
    private boolean isSold;
    private boolean isQuantity;
    private boolean isCurrentPriceChange;

    private String is_flash;

    public String getIs_flash() {
        return is_flash;
    }

    public void setIs_flash(String is_flash) {
        this.is_flash = is_flash;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

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

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getProduct_status() {
        return product_status;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }

    public int getProduct_status_number() {
        return product_status_number;
    }

    public void setProduct_status_number(int product_status_number) {
        this.product_status_number = product_status_number;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

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

    public boolean getIsCurrentPriceChange() {
        return isCurrentPriceChange;
    }

    public void setIsCurrentPriceChange(boolean currentPriceChange) {
        isCurrentPriceChange = currentPriceChange;
    }

    public String getFlash_down_second() {
        return flash_down_second;
    }

    public void setFlash_down_second(String flash_down_second) {
        this.flash_down_second = flash_down_second;
    }

    public String getDown_per_price() {
        return down_per_price;
    }

    public void setDown_per_price(String down_per_price) {
        this.down_per_price = down_per_price;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getFlash_sale_order_time() {
        return flash_sale_order_time;
    }

    public void setFlash_sale_order_time(String flash_sale_order_time) {
        this.flash_sale_order_time = flash_sale_order_time;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public boolean isQuantity() {
        return isQuantity;
    }

    public void setQuantity(boolean quantity) {
        isQuantity = quantity;
    }

    public boolean isCurrentPriceChange() {
        return isCurrentPriceChange;
    }

    public void setCurrentPriceChange(boolean currentPriceChange) {
        isCurrentPriceChange = currentPriceChange;
    }
}