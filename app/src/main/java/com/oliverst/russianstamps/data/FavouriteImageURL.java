package com.oliverst.russianstamps.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_images_url")
public class FavouriteImageURL extends ImageUrl{

    @Ignore
    public FavouriteImageURL(ImageUrl imageUrl) {
        super(imageUrl.getIdStamp(), imageUrl.getUrl());
    }

    public FavouriteImageURL(long id, int idStamp, String url) {
        super(id, idStamp, url);
    }


}
