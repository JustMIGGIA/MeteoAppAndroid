package ch.supsi.dti.isin.meteoapp;

import org.openweathermap.api.model.currentweather.CurrentWeather;

public interface OnTaskCompleted {
    void onTaskCompleted(CurrentWeather currentWeather);
}
