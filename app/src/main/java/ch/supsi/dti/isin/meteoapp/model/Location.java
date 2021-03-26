package ch.supsi.dti.isin.meteoapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

import java.util.UUID;

@Entity(tableName = "location")
public class Location {


    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    @ColumnInfo(name = "city_name")
    private String mName;

    public Location() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        mName = name;
    }

}