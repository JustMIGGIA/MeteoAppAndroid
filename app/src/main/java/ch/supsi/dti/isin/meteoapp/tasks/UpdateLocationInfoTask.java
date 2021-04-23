package ch.supsi.dti.isin.meteoapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ch.supsi.dti.isin.meteoapp.Constants;
import ch.supsi.dti.isin.meteoapp.DBManager;
import ch.supsi.dti.isin.meteoapp.OpenWeatherResponseParser;
import ch.supsi.dti.isin.meteoapp.model.Location;

public class UpdateLocationInfoTask extends AsyncTask<List<Location>, Void, List<Location>> {
    public UpdateLocationInfoTask() {
    }

    @Override
    protected List<Location> doInBackground(List<Location>... lists) {
        List<Location> locations = lists[0];

        if(locations.isEmpty())
            locations.addAll(DBManager.getInstance().locationDao().getLocations());
        else
            locations = DBManager.getInstance().locationDao().getLocations();

        int index = 0;
        for (Location loc : locations){
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="+ loc.getName() +"&units=metric&lang=it&appid=" + Constants.KEY);
                Log.i(Constants.OPEN_WEATHER, url.toString() );
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                StringBuilder stringBuilder = new StringBuilder();

                String input;
                while ((input = bufferedReader.readLine()) != null)
                    stringBuilder.append(input);

                bufferedReader.close();
                in.close();

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                String locId = loc.getId();
                loc = OpenWeatherResponseParser.getInstance().getLocationInfo(jsonObject);
                loc.setId(locId);

                Log.i(Constants.OPEN_WEATHER, loc.toString());

                locations.set(index, loc);
                DBManager.getInstance().locationDao().updateLocation(loc);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            index++;
        }

        return locations;
    }

}
