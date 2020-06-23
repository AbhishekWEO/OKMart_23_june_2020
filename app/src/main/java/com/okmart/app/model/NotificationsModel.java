package com.okmart.app.model;

public class NotificationsModel {

    private String is_read;
    private String is_clear;
    private String status;
    private String status_ref;
    private String notification_ref;
    private String heading;
    private String message;
    private String order_status;


    public String getNotification_ref() {
        return notification_ref;
    }

    public void setNotification_ref(String notification_ref) {
        this.notification_ref = notification_ref;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIs_clear() {
        return is_clear;
    }

    public void setIs_clear(String is_clear) {
        this.is_clear = is_clear;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_ref() {
        return status_ref;
    }

    public void setStatus_ref(String status_ref) {
        this.status_ref = status_ref;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
