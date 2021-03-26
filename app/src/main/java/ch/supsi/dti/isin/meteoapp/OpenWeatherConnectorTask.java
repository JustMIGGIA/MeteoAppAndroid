package ch.supsi.dti.isin.meteoapp;

import android.os.AsyncTask;
import android.util.Log;

import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.query.Language;
import org.openweathermap.api.query.QueryBuilderPicker;
import org.openweathermap.api.query.ResponseFormat;
import org.openweathermap.api.query.Type;
import org.openweathermap.api.query.UnitFormat;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;

public class OpenWeatherConnectorTask extends AsyncTask<String, Void, CurrentWeather> {

    private final String KEY = "d57196df619f8d3c9fc448dc316db8f8";

    public OpenWeatherConnectorTask(){}

    @Override
    protected CurrentWeather doInBackground(String... strings) {
        DataWeatherClient dataWeatherClient = new UrlConnectionDataWeatherClient(KEY);
        CurrentWeatherOneLocationQuery currentWeatherOneLocationQuery = QueryBuilderPicker.pick()
                .currentWeather()
                .oneLocation()
                .byCityName(strings[0])
                .type(Type.ACCURATE)
                .language(Language.ITALIAN)
                .responseFormat(ResponseFormat.JSON)
                .unitFormat(UnitFormat.METRIC)
                .build();
        CurrentWeather currentWeather = dataWeatherClient.getCurrentWeather(currentWeatherOneLocationQuery);

        Log.i("json", prettyPrint(currentWeather));

        return currentWeather;
    }


    private static String prettyPrint(CurrentWeather currentWeather) {
        return String.format(
                "Current weather in %s(%s):\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                currentWeather.getCityName(), currentWeather.getSystemParameters().getCountry(),
                currentWeather.getMainParameters().getTemperature(),
                currentWeather.getMainParameters().getHumidity(),
                currentWeather.getMainParameters().getPressure()
        );
    }

}