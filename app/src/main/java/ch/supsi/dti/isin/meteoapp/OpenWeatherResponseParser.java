package ch.supsi.dti.isin.meteoapp;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import ch.supsi.dti.isin.meteoapp.model.Location;

public class OpenWeatherResponseParser {
    private static OpenWeatherResponseParser instance;

    public OpenWeatherResponseParser() {
    }

    public static OpenWeatherResponseParser getInstance() {
        if(instance == null)
            instance = new OpenWeatherResponseParser();
        return instance;
    }

    public Location getLocationInfo(JSONObject jsonObject){
        Location location = new Location();

        try {
            location.setName(jsonObject.getString("name").replace("\"",""));

            JSONObject weather = new JSONObject(jsonObject.getString("weather").replace("[","").replace("]",""));

            JSONObject main = jsonObject.getJSONObject("main");

            location.setWeather_descr(weather.getString("description").replace("\"",""));

            String stringBuilder = "x" +
                    weather.getString("icon").replace("\"", "") +
                    "2x";
            location.setWeather_icon(stringBuilder);

            location.setTemp(Math.floor(main.getDouble("temp") * 10) / 10);
            location.setTemp_min(Math.floor(main.getDouble("temp_min") * 10) / 10);
            location.setTemp_max(Math.floor(main.getDouble("temp_max") * 10) / 10);
            location.setPressure(Math.floor(main.getDouble("pressure") * 10) / 10);
            location.setHumidity(Math.floor(main.getDouble("humidity") * 10) / 10);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return location;
    }
}
