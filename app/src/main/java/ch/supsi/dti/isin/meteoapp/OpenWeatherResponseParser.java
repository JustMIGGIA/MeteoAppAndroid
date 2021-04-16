package ch.supsi.dti.isin.meteoapp;


import org.json.JSONException;
import org.json.JSONObject;

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

            location.setTemp(main.getDouble("temp"));
            location.setTemp_min(main.getDouble("temp_min"));
            location.setTemp_max(main.getDouble("temp_max"));
            location.setPressure(main.getDouble("pressure"));
            location.setHumidity(main.getDouble("humidity"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return location;
    }
}
