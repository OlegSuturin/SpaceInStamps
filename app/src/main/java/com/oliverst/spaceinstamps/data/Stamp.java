package com.oliverst.spaceinstamps.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName ="stamps")
public class Stamp{
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int idStamp;   //*
    private String theme;
    private String country;
    private int year;   //*
    private String dateRelease;
    private String name;           //*
    private String overview;
    private String specifications;    //тираж
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

    public Stamp(long id, int idStamp, String theme, String country, int year, String dateRelease, String name, String overview, String specifications, String quantity, String catalogNumberITC, String catalogNumberSK, String catalogNumberMich, String catalogNumberScott, String catalogNumberCgib, String price, String photoPath, String bigPhotoPath, String detailUrl, boolean flag) {
        this.id = id;
        this.idStamp = idStamp;
        this.theme = theme;
        this.country = country;
        this.year = year;
        this.dateRelease = dateRelease;
        this.name = name;
        this.overview = overview;
        this.specifications = specifications;
        this.quantity = quantity;
        this.catalogNumberITC = catalogNumberITC;
        this.catalogNumberSK = catalogNumberSK;
        this.catalogNumberMich = catalogNumberMich;
        this.catalogNumberScott = catalogNumberScott;
        this.catalogNumberCgib = catalogNumberCgib;
        this.price = price;
        this.photoPath = photoPath;
        this.bigPhotoPath = bigPhotoPath;
        this.detailUrl = detailUrl;
        this.flag = flag;
    }

    @Ignore
    public Stamp(int idStamp, int year, String name, String quantity, String catalogNumberITC, String catalogNumberSK, String catalogNumberMich, String price, String detailUrl) {
        this.idStamp = idStamp;
        this.year = year;
        this.name = name;
        this.quantity = quantity;
        this.catalogNumberITC = catalogNumberITC;
        this.catalogNumberSK = catalogNumberSK;
        this.catalogNumberMich = catalogNumberMich;
        this.price = price;
        this.detailUrl = detailUrl;

        this.flag = false;
    }
    @Ignore
    public Stamp(String country, String dateRelease, String overview, String specifications) {
        this.country = country;
        this.dateRelease = dateRelease;
        this.overview = overview;
        this.specifications = specifications;
        this.bigPhotoPath = bigPhotoPath;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdStamp(int idStamp) {
        this.idStamp = idStamp;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDateRelease(String dateRelease) {
        this.dateRelease = dateRelease;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setCatalogNumberITC(String catalogNumberITC) {
        this.catalogNumberITC = catalogNumberITC;
    }

    public void setCatalogNumberSK(String catalogNumberSK) {
        this.catalogNumberSK = catalogNumberSK;
    }

    public void setCatalogNumberMich(String catalogNumberMich) {
        this.catalogNumberMich = catalogNumberMich;
    }

    public void setCatalogNumberScott(String catalogNumberScott) {
        this.catalogNumberScott = catalogNumberScott;
    }

    public void setCatalogNumberCgib(String catalogNumberCgib) {
        this.catalogNumberCgib = catalogNumberCgib;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setBigPhotoPath(String bigPhotoPath) {
        this.bigPhotoPath = bigPhotoPath;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public long getId() {
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

    public int getYear() {
        return year;
    }

    public String getDateRelease() {
        return dateRelease;
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
