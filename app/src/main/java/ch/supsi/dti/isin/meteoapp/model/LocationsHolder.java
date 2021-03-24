package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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

        Thread th = new Thread(() -> mLocations.addAll(DBManager.getInstance(context).locationDao().getLocations()));

        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public Location getLocation(String id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }
        return null;
    }
}
