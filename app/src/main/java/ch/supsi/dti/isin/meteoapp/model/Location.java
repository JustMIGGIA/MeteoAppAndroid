package ch.supsi.dti.isin.meteoapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.util.UUID;

@Entity(tableName = "location")
public class Location {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    private String name;

    private String weather_descr;
    private String weather_icon;

    private double temp;
    private double temp_min;
    private double temp_max;
    private double pressure;
    private double humidity;


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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getWeather_descr() {
        return weather_descr;
    }

    public void setWeather_descr(String weather_descr) {
        this.weather_descr = weather_descr;
    }

    public String getWeather_icon() {
        return weather_icon;
    }

    public void setWeather_icon(String weather_icon) {
        this.weather_icon = weather_icon;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", weather_descr='" + weather_descr + '\'' +
                ", weather_icon='" + weather_icon + '\'' +
                ", temp=" + temp +
                ", temp_min=" + temp_min +
                ", temp_max=" + temp_max +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                '}';
    }
}