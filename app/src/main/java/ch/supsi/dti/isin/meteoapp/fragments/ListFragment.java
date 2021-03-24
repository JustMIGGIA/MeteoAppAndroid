package ch.supsi.dti.isin.meteoapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import ch.supsi.dti.isin.meteoapp.DBManager;
import ch.supsi.dti.isin.meteoapp.OpenWeatherConnector;
import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;


public class ListFragment extends Fragment {
    private RecyclerView mLocationRecyclerView;
    private LocationAdapter mAdapter;
    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dbManager = DBManager.getInstance(getContext());

        OpenWeatherConnector.getInstance().init(getContext());
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

    private void showAddItemDialog(Context c){
        final EditText text = new EditText(c);

        AlertDialog dialog =  new AlertDialog.Builder(c)
                .setTitle("Add a new location")
                .setView(text)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(text.getText());
                        if (!task.equals("")){
                            Location location = new Location();
                            location.setName(task);

                            new Thread(() -> {
                                if(dbManager.locationDao().getLocation(location.getName()) != null){

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast toast = Toast.makeText(getActivity(),
                                                "Location already present",
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    });

                                } else {
                                    long id = dbManager.locationDao().insertLocation(location);

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast toast = Toast.makeText(getActivity(),
                                                "Location added",
                                                Toast.LENGTH_SHORT);
                                        toast.show();

                                        //OpenWeatherConnector.getInstance().getWeatherByCityName(location);
                                        //Log.i("json", location.getDetails().toString());

                                        mAdapter.mLocations.add(location);
                                        mAdapter.notifyItemInserted((int) id);
                                    });
                                }

                            }).start();

                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                showAddItemDialog(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Holder

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView mNameTextView;
        private Location mLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            if(mLocation.getName().equals("Gps")){

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    Log.i("GPS", "Permission granted");

                    if(!SmartLocation.with(getActivity()).location().state().isGpsAvailable()){
                        Log.i("GPS", "GPS is disabled");

                        Toast toast = Toast.makeText(getActivity(),
                                "GPS non attivo",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        LocationParams.Builder builder = new LocationParams.Builder()
                                .setAccuracy(LocationAccuracy.HIGH);

                        SmartLocation.with(getActivity()).location().oneFix().config(builder.build())
                                .start(location -> {
                                    Log.i("GPS", "Location" + location);

                                    //OpenWeatherConnector.getInstance().getWeatherByCoords(location.getLatitude(), location.getLongitude());

                                    Toast toast = Toast.makeText(getActivity(),
                                            location.getLatitude() + " " + location.getLongitude(),
                                            Toast.LENGTH_SHORT);
                                    toast.show();

                                });
                    }
                }


            }else{
                OpenWeatherConnector.getInstance().getWeatherByCityName(mLocation);
                Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
                startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(!mLocation.getName().equals("Gps")) {

                int index = mAdapter.mLocations.indexOf(mLocation);
                mAdapter.mLocations.remove(mLocation);
                mAdapter.notifyItemRemoved(index);

                new Thread( () -> dbManager.locationDao().deleteLocation(mLocation)).start();
            }

            return true;
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
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
