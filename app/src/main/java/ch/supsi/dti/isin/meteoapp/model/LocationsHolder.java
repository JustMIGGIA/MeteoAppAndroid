package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.DBManager;

public class LocationsHolder {

    private static LocationsHolder sLocationsHolder;
    private List<Location> mLocations;

    public static LocationsHolder get(Context context) {
        if (sLocationsHolder == null)
            sLocationsHolder = new LocationsHolder(context);

        return sLocationsHolder;
    }

    private LocationsHolder(Context context) {
        mLocations = new ArrayList<>();
        Location location = new Location();
        location.setName("GPS");
        mLocations.add(location);

        Cursor c = DBManager.getInstance().getAllCities();
        if(c.moveToFirst()){
            do{
                Location loc = new Location();
                loc.setName(c.getString(1));
                mLocations.add(loc);
            }while(c.moveToNext());
        }
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public Location getLocation(UUID id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }

        return null;
    }
}
