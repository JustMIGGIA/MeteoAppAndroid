package ch.supsi.dti.isin.meteoapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.tasks.UpdateLocationInfoTask;

public class UpadateWorker extends Worker {

    private static final double TEMP_MIN =  10;
    private static final double TEMP_MAX =  20;


    public UpadateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        UpdateLocationInfoTask updateLocationInfoTask = new UpdateLocationInfoTask();
        try {

            List<Location> updatedList = updateLocationInfoTask.execute(LocationsHolder.get(getApplicationContext()).getLocations()).get();

            StringBuilder stringBuilder = new StringBuilder();

            for (Location location : updatedList){
                if(location.getTemp_min() < TEMP_MIN)
                    stringBuilder.append(location.getName().toUpperCase() + " :\n " + "min_temp = " + location.getTemp_min() + "\n");

                if(location.getTemp_min() > TEMP_MAX)
                    stringBuilder.append(location.getName().toUpperCase() + " :\n " + "max_temp = " + location.getTemp_max() + "\n");
            }

            if(stringBuilder.length() != 0){
                NotificationCompat.Builder mBuilder = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(),"default")
                            .setSmallIcon(android.R.drawable.ic_menu_day)
                            .setContentTitle(LocalTime.now().toString())
                            .setContentText(stringBuilder.toString())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                }

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                managerCompat.notify(0, mBuilder.build());
            }


            return Result.success();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
