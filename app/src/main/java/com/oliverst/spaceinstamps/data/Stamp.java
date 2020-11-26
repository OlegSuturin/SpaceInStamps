package com.oliverst.spaceinstamps.data;

public class Stamp{
    int id;
    private String theme;
    private String country;
    private String releaseYear;
    private String name;
    private String overview;
    private String quantity;
    private String catalogNumber;
    private String price;

    private String photoPath;
    private String bigPhotoPath;

    public Stamp(int id, String theme, String country, String releaseYear, String name, String overview, String quantity, String catalogNumber, String price, String photoPath, String bigPhotoPath) {
        this.id = id;
        this.theme = theme;
        this.country = country;
        this.releaseYear = releaseYear;
        this.name = name;
        this.overview = overview;
        this.quantity = quantity;
        this.catalogNumber = catalogNumber;
        this.price = price;
        this.photoPath = photoPath;
        this.bigPhotoPath = bigPhotoPath;
    }

    public int getId() {
        return id;
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

    public String getCatalogNumber() {
        return catalogNumber;
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
}
