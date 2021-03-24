package ch.supsi.dti.isin.meteoapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.supsi.dti.isin.meteoapp.model.Location;

@Database(entities = {Location.class}, version = 1, exportSchema = false)
public abstract class DBManager extends RoomDatabase {

    public static final String DATABASE_NAME = "location_db";
    private static DBManager instance;

    public static DBManager getInstance(Context context) {
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DBManager.class, DBManager.DATABASE_NAME).build();
        return instance;
    }

    public abstract LocationDao locationDao();
}
