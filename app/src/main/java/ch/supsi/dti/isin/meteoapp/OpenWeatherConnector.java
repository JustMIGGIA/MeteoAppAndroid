package ch.supsi.dti.isin.meteoapp;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import ch.supsi.dti.isin.meteoapp.activities.MainActivity;
import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenWeatherConnector {
    private static OpenWeatherConnector instance;
    //private RequestQueue requestQueue;
    private final String URL = "https://api.openweathermap.org/data/2.5/weather?";
    private final String KEY = "&appid=d57196df619f8d3c9fc448dc316db8f8";

    private OpenWeatherConnector() {
    }

    public static OpenWeatherConnector getInstance() {
        if(instance == null)
            instance = new OpenWeatherConnector();
        return instance;
    }

    public void init(Context context){
       // requestQueue = Volley.newRequestQueue(context);
    }

    public void getWeatherByCoords(Location location){

//        String url = URL + "lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + KEY;
//        Log.i("json", url);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                // TODO: 04/03/2021 Parsing json
//                Log.i("json", "TUTTO OK");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Busy
//                Log.i("json", error.getMessage());
//            }
//        });
//
//        requestQueue.add(jsonObjectRequest);
    }

    public void getWeatherByCityName(String cityname) {
        String str = URL + "q=" + cityname + KEY;
        Log.i("json", str);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(str)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("json", Arrays.toString(e.getStackTrace()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Log.i("json",jsonObject.toString());

                    if(jsonObject.getString("cod").equals("404")){
                        Log.i("json", "CIAO");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                // TODO: 04/03/2021 Parsing json
//                Log.i("json", "TUTTO OK");
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Busy
//                Log.i("json", "City not found");
//            }
//        });
//
//        requestQueue.add(jsonObjectRequest);

    }
}
