package com.oliverst.russianstamps.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_stamp")
public class FavouriteStamp extends Stamp {

    public FavouriteStamp(long id, int idStamp, String theme, String country, int year, String dateRelease, String name, String overview, String specifications, String quantity, String catalogNumberITC, String catalogNumberSK, String catalogNumberMich, String catalogNumberScott, String catalogNumberCgib, String price, String photoPath, String bigPhotoPath, String detailUrl, boolean flag) {
        super(id, idStamp, theme, country, year, dateRelease, name, overview, specifications, quantity, catalogNumberITC, catalogNumberSK, catalogNumberMich, catalogNumberScott, catalogNumberCgib, price, photoPath, bigPhotoPath, detailUrl, flag);
    }

    @Ignore
    public FavouriteStamp(Stamp stamp) {
        super(
                stamp.getIdStamp(),
                stamp.getTheme(),
                stamp.getCountry(),
                stamp.getYear(),
                stamp.getDateRelease(),
                stamp.getName(),
                stamp.getOverview(),
                stamp.getSpecifications(),
                stamp.getQuantity(),
                stamp.getCatalogNumberITC(),
                stamp.getCatalogNumberSK(),
                stamp.getCatalogNumberMich(),
                stamp.getCatalogNumberScott(),
                stamp.getCatalogNumberCgib(),
                stamp.getPrice(),
                stamp.getPhotoPath(),
                stamp.getPhotoPath(),
                stamp.getDetailUrl(),
                stamp.isFlag());
    }

}
