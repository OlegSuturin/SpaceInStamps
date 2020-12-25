package com.oliverst.russianstamps.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Stamp.class, ImageUrl.class, FavouriteStamp.class, FavouriteImageURL.class}, version = 13, exportSchema = false)
public abstract class StampDatabase extends RoomDatabase {
    private static StampDatabase database;
    private static final String DB_NAME = "stamp.db";
    private static final Object LOCK = new Object();

    public static StampDatabase getInstance(Context context){
            synchronized (LOCK){
                if(database == null){
                    database = Room.databaseBuilder(context, StampDatabase.class, DB_NAME).
                            fallbackToDestructiveMigration().
                             build();
                }
            }
         return  database;
    }

    public abstract StampDao stampDao();
}
