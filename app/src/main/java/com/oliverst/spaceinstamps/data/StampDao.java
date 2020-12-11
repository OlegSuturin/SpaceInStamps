package com.oliverst.spaceinstamps.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface StampDao {

    //табл favourite_stamp
    @Query("SELECT * FROM favourite_stamp")
    LiveData<List<FavouriteStamp>> getAllFavouriteStamps();

    @Query("SELECT * FROM favourite_stamp WHERE idStamp == :id")
    FavouriteStamp getFavouriteStampByIdStamp(int id);

    @Query("SELECT * FROM favourite_stamp WHERE id == :id")
    FavouriteStamp getFavouriteStampById(long id);

    @Insert
    void insertFavouriteStamp(FavouriteStamp favouriteStamp);

    @Delete
    void deleteFavouriteStamp(FavouriteStamp favouriteStamp);

    @Query("SELECT COUNT(*) FROM favourite_stamp")
    int getItemCountFavouriteStamps();


    //табл stamps
    @Query("SELECT * FROM stamps")
    LiveData<List<Stamp>> getAllStamps();

    @Query("SELECT * FROM stamps WHERE idStamp == :id")
    Stamp getStampByIdStamp(int id);

    @Query("SELECT * FROM stamps WHERE id == :id")
    Stamp getStampById(long id);

    @Insert
    void insertStamp(Stamp stamp);

    @Update
    void updateStamp(Stamp stamp);

    @Delete
    void deleteStamp(Stamp stamp);

    @Query("DELETE FROM stamps")
    void deleteAllStamps();

    @Query("SELECT COUNT(*) FROM stamps")
    int getItemCountStamps();

// таблица images_url
    @Query("SELECT * FROM images_url")
    LiveData<List<ImageUrl>> getAllImagesUrl();

     @Query("SELECT * FROM images_url WHERE idStamp == :id")
     List<ImageUrl> getImagesUrlById(int id);

    @Insert
    void insertImageUrl(ImageUrl imageUrl);

    @Query("DELETE FROM images_url")
    void  deleteAllImagesUrl();

// таблица favourite_images_url

    @Query("SELECT * FROM favourite_images_url")
    LiveData<List<FavouriteImageURL>> getAllFavouriteImagesUrl();

    @Query("SELECT * FROM favourite_images_url WHERE idStamp == :id")
    List<FavouriteImageURL> getFavouriteImagesUrlById(int id);

    @Insert
    void insertFavouriteImageUrl(FavouriteImageURL favouriteImageURL);

    @Query("DELETE FROM favourite_images_url")
    void  deleteAllFavouriteImagesUrl();

    @Query("DELETE FROM favourite_images_url WHERE idStamp == :id")
    void deleteFavouriteImagesUrlById(int id);

}
