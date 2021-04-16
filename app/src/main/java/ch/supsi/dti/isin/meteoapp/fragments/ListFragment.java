package ch.supsi.dti.isin.meteoapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.supsi.dti.isin.meteoapp.Constants;
import ch.supsi.dti.isin.meteoapp.DBManager;
import ch.supsi.dti.isin.meteoapp.OnDialogResultListener;
import ch.supsi.dti.isin.meteoapp.UpadateWorker;
import ch.supsi.dti.isin.meteoapp.tasks.GetByCoordsTask;
import ch.supsi.dti.isin.meteoapp.tasks.GetByNameTask;
import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;


public class ListFragment extends Fragment implements OnDialogResultListener {
    private RecyclerView mLocationRecyclerView;
    private LocationAdapter mAdapter;
    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbManager = DBManager.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mLocationRecyclerView = view.findViewById(R.id.recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Location> locations = LocationsHolder.get(getActivity()).getLocations();
        mAdapter = new LocationAdapter(locations);
        mLocationRecyclerView.setAdapter(mAdapter);

        return view;
    }

    // Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list, menu);
    }


    /***************************************************************************************
     *                              LOCATION INSERTION                                     *
     ***************************************************************************************/

    @Override
    public void onDialogResult(String result) {

        GetByNameTask getByNameTask = new GetByNameTask();
        Location location = null;

        try {
             location = getByNameTask.execute(result).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (location == null){
            Toast.makeText(getActivity(),
                    "Inexistent location",
                    Toast.LENGTH_SHORT)
            .show();
            return;
        }

        Location finalLocation = location;
        new Thread(() -> {
            if(dbManager.locationDao().getLocation(finalLocation.getName()) != null){

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast toast = Toast.makeText(getActivity(),
                            "Location already present",
                            Toast.LENGTH_SHORT);
                    toast.show();
                });

            } else {
                long id = dbManager.locationDao().insertLocation(finalLocation);

                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast toast = Toast.makeText(getActivity(),
                            "Location added",
                            Toast.LENGTH_SHORT);
                    toast.show();

                    mAdapter.mLocations.add(finalLocation);
                    mAdapter.notifyItemInserted((int) id);
                });
            }

        }).start();
    }

    private void showDialogAndGetresult(final String title, final String message, final String initialText, final OnDialogResultListener listener){

        final EditText editText = new EditText(getContext());
        editText.setText(initialText);

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Add", (dialog, which) -> {
                    if(listener != null)
                        if(!editText.getText().toString().equals(""))
                            listener.onDialogResult(editText.getText().toString());
                })
                .setView(editText)
                .show();
    }


    /***************************************************************************************
     *                       GPS PERMISSION AND LOCATION INSERTION                         *
     ***************************************************************************************/

    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            startLocationListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationListener();
                return;
        }
    }

    private void startLocationListener(){
        if(!SmartLocation.with(getActivity()).location().state().isGpsAvailable()){
            Toast toast = Toast.makeText(getActivity(),
                    "GPS is disabled",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            LocationParams.Builder builder = new LocationParams.Builder()
                    .setAccuracy(LocationAccuracy.HIGH);

            SmartLocation.with(getActivity()).location().oneFix().config(builder.build())
                    .start(location -> {
                        Log.i(Constants.GPS, "Location" + location);

                        GetByCoordsTask getByCoordsTask = new GetByCoordsTask();

                        Location loc = null;
                        try {
                            loc = getByCoordsTask.execute(location.getLatitude(), location.getLongitude()).get();


                            Location finalLoc = loc;
                            new Thread(() -> {
                                if(dbManager.locationDao().getLocation(finalLoc.getName()) != null){

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast toast = Toast.makeText(getActivity(),
                                                "Location already present",
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    });

                                } else {
                                    long id = dbManager.locationDao().insertLocation(finalLoc);

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast toast = Toast.makeText(getActivity(),
                                                "Location added",
                                                Toast.LENGTH_SHORT);
                                        toast.show();

                                        mAdapter.mLocations.add(finalLoc);
                                        mAdapter.notifyItemInserted((int) id);
                                    });
                                }

                            }).start();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        Toast toast = Toast.makeText(getActivity(),
                                location.getLatitude() + " " + location.getLongitude(),
                                Toast.LENGTH_SHORT);
                        toast.show();

                    });
        }
    }

    private void addGpsLocation(){
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(Constants.GPS, "Permission not granted");
            requestPermission();
        } else {
            Log.i(Constants.GPS, "Permission granted");
            startLocationListener();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                showDialogAndGetresult("Add location", null, "",this);
                return true;
            case R.id.menu_gps:
                addGpsLocation();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Holder

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mNameTextView;
        private TextView mDegreeTextView;
        private ImageView mImageView;
        private Location mLocation;


        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
            mDegreeTextView = itemView.findViewById(R.id.degrees);
            mImageView = itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {

            int index = mAdapter.mLocations.indexOf(mLocation);
            mAdapter.mLocations.remove(mLocation);
            mAdapter.notifyItemRemoved(index);

            new Thread( () -> dbManager.locationDao().deleteLocation(mLocation)).start();

            return true;
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
            mDegreeTextView.setText(mLocation.getTemp() + "°");
            int id = getContext().getResources().getIdentifier(mLocation.getWeather_icon(), "drawable", getContext().getPackageName());
            mImageView.setImageResource(id);

        }
    }

    // Adapter

    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private final List<Location> mLocations;

        public LocationAdapter(List<Location> locations) {
            mLocations = locations;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            Location location = mLocations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }
    }
}
