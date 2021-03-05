package ch.supsi.dti.isin.meteoapp.model;

import java.util.UUID;

public class Location {
    private UUID Id;
    private String mName;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        mName = name;
    }

    public Location() {
        Id = UUID.randomUUID();
    }
}