package com.okmart.app.model;

import java.io.Serializable;
import java.util.HashMap;

public class PoliciesModel implements Serializable {

    private String id;
    private String title;
    private String page_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }
}
