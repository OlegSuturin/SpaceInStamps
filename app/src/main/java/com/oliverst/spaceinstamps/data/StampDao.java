package com.oliverst.spaceinstamps.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StampDao {

    @Query("SELECT * FROM stamps")
    LiveData<List<Stamp>> getAllStamps();

    @Query("SELECT * FROM stamps WHERE idStamp == :id")
    Stamp getStampById(int id);

    @Insert
    void insertStamp(Stamp stamp);

    @Delete
    void deleteStamp(Stamp stamp);

    @Query("DELETE FROM stamps")
    void deleteAllStamps();


}
