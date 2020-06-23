package com.okmart.app.model;

public class CategoryData {

    private String name;
    private String cat_icon;
    private String cat_selected_icon;
    private boolean isCheck;
    private String category_ref;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCat_icon() {
        return cat_icon;
    }

    public void setCat_icon(String cat_icon) {
        this.cat_icon = cat_icon;
    }

    public String getCat_selected_icon() {
        return cat_selected_icon;
    }

    public void setCat_selected_icon(String cat_selected_icon) {
        this.cat_selected_icon = cat_selected_icon;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getCategory_ref() {
        return category_ref;
    }

    public void setCategory_ref(String category_ref) {
        this.category_ref = category_ref;
    }
}
