package com.vickyjha.chatter.Data;

import  java.io.Serializable;


public class ItemModelData implements java.io.Serializable{
    private String imageId;
    private String name;
    private String phoneNo;
    private String id;
    private boolean selected;
    public ItemModelData(String imageId,String id, String name,String phoneNo, boolean selected){
        this.imageId = imageId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.id = id;
        this.selected = selected;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
