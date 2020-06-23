package com.okmart.app.model;

public class StateListModel {

    private String name;
    private String state_ref;
    private String subdivision;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState_ref() {
        return state_ref;
    }

    public void setState_ref(String state_ref) {
        this.state_ref = state_ref;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
