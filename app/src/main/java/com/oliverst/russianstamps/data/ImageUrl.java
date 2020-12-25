package com.oliverst.russianstamps.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "images_url")
public class ImageUrl {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int idStamp;
    private String url;

    public ImageUrl(long id, int idStamp, String url) {
        this.id = id;
        this.idStamp = idStamp;
        this.url = url;
    }
    @Ignore
    public ImageUrl(int idStamp, String url) {
        this.idStamp = idStamp;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public int getIdStamp() {
        return idStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdStamp(int idStamp) {
        this.idStamp = idStamp;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

