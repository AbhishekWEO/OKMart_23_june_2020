package com.okmart.app.model;

public class MyBiddings
{
    private String user_name;
    private String user_ref;
    private String transaction_id;
    private String actual_price;
    private String current_price;
    private String bid_price;
    private String shipping_price;
    private String is_bid_cancelled;
    private String bid_datetime;
    private String bid_time;
    private String is_winner;
    private String is_edited;
    private String is_direct_checkout;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_ref() {
        return user_ref;
    }

    public void setUser_ref(String user_ref) {
        this.user_ref = user_ref;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getActual_price() {
        return actual_price;
    }

    public void setActual_price(String actual_price) {
        this.actual_price = actual_price;
    }

    public String getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(String current_price) {
        this.current_price = current_price;
    }

    public String getBid_price() {
        return bid_price;
    }

    public void setBid_price(String bid_price) {
        this.bid_price = bid_price;
    }

    public String getShipping_price() {
        return shipping_price;
    }

    public void setShipping_price(String shipping_price) {
        this.shipping_price = shipping_price;
    }

    public String getIs_bid_cancelled() {
        return is_bid_cancelled;
    }

    public void setIs_bid_cancelled(String is_bid_cancelled) {
        this.is_bid_cancelled = is_bid_cancelled;
    }

    public String getBid_datetime() {
        return bid_datetime;
    }

    public void setBid_datetime(String bid_datetime) {
        this.bid_datetime = bid_datetime;
    }

    public String getBid_time() {
        return bid_time;
    }

    public void setBid_time(String bid_time) {
        this.bid_time = bid_time;
    }

    public String getIs_winner() {
        return is_winner;
    }

    public void setIs_winner(String is_winner) {
        this.is_winner = is_winner;
    }

    public String getIs_edited() {
        return is_edited;
    }

    public void setIs_edited(String is_edited) {
        this.is_edited = is_edited;
    }

    public String getIs_direct_checkout() {
        return is_direct_checkout;
    }

    public void setIs_direct_checkout(String is_direct_checkout) {
        this.is_direct_checkout = is_direct_checkout;
    }
}