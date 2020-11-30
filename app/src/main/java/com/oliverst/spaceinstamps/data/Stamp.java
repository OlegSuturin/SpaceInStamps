package com.oliverst.spaceinstamps.data;

public class Stamp{
    int id;
    int idStamp;   //*
    private String theme;
    private String country;
    private String year;   //*
    private String dataRelease;
    private String name;           //*
    private String overview;
    private String quantity;        //*
    private String catalogNumberITC;    //*
    private String catalogNumberSK;     //*
    private String catalogNumberMich;   //*
    private String catalogNumberScott;
    private String catalogNumberCgib;
    private String price;               //*

    private String photoPath;
    private String bigPhotoPath;
    private String detailUrl;           //*
    private boolean flag;

    public Stamp(int idStamp, String year, String name, String quantity, String catalogNumberITC, String catalogNumberSK, String catalogNumberMich, String price, String detailUrl) {
        this.idStamp = idStamp;
        this.year = year;
        this.name = name;
        this.quantity = quantity;
        this.catalogNumberITC = catalogNumberITC;
        this.catalogNumberSK = catalogNumberSK;
        this.catalogNumberMich = catalogNumberMich;
        this.price = price;
        this.detailUrl = detailUrl;
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

    public String getYear() {
        return year;
    }

    public String getDataRelease() {
        return dataRelease;
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

    public String getCatalogNumberCgib() {
        return catalogNumberCgib;
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

    public String getDetailUrl() {
        return detailUrl;
    }

    public boolean isFlag() {
        return flag;
    }
}
