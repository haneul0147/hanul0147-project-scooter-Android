package com.jhn.googlemaptest.model;

import java.io.Serializable;

public class ImageReq implements Serializable {
    private int id;
    private String img_url;



    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
