package ch.supsi.dti.isin.meteoapp.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import ch.supsi.dti.isin.meteoapp.UpadateWorker;
import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "TEMP_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Temperature Channel Notification");
            mNotificationManager.createNotificationChannel(channel);
        }

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpadateWorker.class,15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("UPDATE", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);

        return new ListFragment();
    }
}
