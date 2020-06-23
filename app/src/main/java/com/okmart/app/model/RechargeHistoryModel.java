package com.okmart.app.model;

public class RechargeHistoryModel {

    private String card_holder_name;
    private String payment_status;
    private String _seconds;
    private String card_last4;
    private String payment_ref;
    private String txn_id;
    private String payment_id;
    private String price;
    private String card_brand;
    private String payment_method;
    private String is_new_user;
    private String referral_user_name;
    private String referral_code;

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public void setCard_holder_name(String card_holder_name) {
        this.card_holder_name = card_holder_name;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String get_seconds() {
        return _seconds;
    }

    public void set_seconds(String _seconds) {
        this._seconds = _seconds;
    }

    public String getCard_last4() {
        return card_last4;
    }

    public void setCard_last4(String card_last4) {
        this.card_last4 = card_last4;
    }

    public String getPayment_ref() {
        return payment_ref;
    }

    public void setPayment_ref(String payment_ref) {
        this.payment_ref = payment_ref;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCard_brand() {
        return card_brand;
    }

    public void setCard_brand(String card_brand) {
        this.card_brand = card_brand;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getIs_new_user() {
        return is_new_user;
    }

    public void setIs_new_user(String is_new_user) {
        this.is_new_user = is_new_user;
    }

    public String getReferral_user_name() {
        return referral_user_name;
    }

    public void setReferral_user_name(String referral_user_name) {
        this.referral_user_name = referral_user_name;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }
}