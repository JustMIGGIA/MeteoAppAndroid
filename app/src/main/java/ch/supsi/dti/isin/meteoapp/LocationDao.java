package ch.supsi.dti.isin.meteoapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ch.supsi.dti.isin.meteoapp.model.Location;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM location")
    List<Location> getLocations();

    @Query("SELECT * FROM location WHERE name = :locationName ")
    Location getLocation(String locationName);

    @Insert
    long insertLocation(Location location);

    @Delete
    void deleteLocation(Location location);
}
