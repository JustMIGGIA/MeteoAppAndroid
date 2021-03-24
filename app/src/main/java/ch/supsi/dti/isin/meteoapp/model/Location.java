package ch.supsi.dti.isin.meteoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.UUID;

@Entity(tableName = "location")
public class Location {

    @NotNull
    @PrimaryKey(autoGenerate = false)
    private String id;

    @ColumnInfo(name = "city_name")
    private String mName;

    @Ignore
    private JSONObject details;

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

    public JSONObject getDetails() {
        return details;
    }

    public void setDetails(JSONObject details) {
        this.details = details;
    }
}