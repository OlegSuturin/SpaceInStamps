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

    //табл stamps
    @Query("SELECT * FROM stamps")
    LiveData<List<Stamp>> getAllStamps();

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
     List<ImageUrl> getImagesUrlById(long id);

    @Insert
    void insertImageUrl(ImageUrl imageUrl);

    @Query("DELETE FROM images_url")
    void  deleteAllImagesUrl();
}
