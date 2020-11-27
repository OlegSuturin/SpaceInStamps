package com.oliverst.spaceinstamps.data;

public class Stamp{
    int id;
    int idStamp;
    private String theme;
    private String country;
    private String releaseYear;
    private String name;
    private String overview;
    private String quantity;
    private String catalogNumberITC;
    private String catalogNumberSK;
    private String catalogNumberMich;
    private String catalogNumberScott;
    private String price;

    private String photoPath;
    private String bigPhotoPath;
    private String resourceUrl;
    private boolean flag;

    public Stamp(int idStamp, String theme, String name, String quantity) {
        this.idStamp = idStamp;
        this.theme = theme;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getIdStamp() {
        return idStamp;
    }

    public String getTheme() {
        return theme;
    }

    public String getCountry() {
        return country;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCatalogNumberITC() {
        return catalogNumberITC;
    }

    public String getCatalogNumberSK() {
        return catalogNumberSK;
    }

    public String getCatalogNumberMich() {
        return catalogNumberMich;
    }

    public String getCatalogNumberScott() {
        return catalogNumberScott;
    }

    public String getPrice() {
        return price;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getBigPhotoPath() {
        return bigPhotoPath;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public boolean isFlag() {
        return flag;
    }
}
