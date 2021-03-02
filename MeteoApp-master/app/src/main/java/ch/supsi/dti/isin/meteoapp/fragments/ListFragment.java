package ch.supsi.dti.isin.meteoapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.activities.MainActivity;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class ListFragment extends Fragment {
    private RecyclerView mLocationRecyclerView;
    private LocationAdapter mAdapter;
    private DBManager db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new DBManager(getContext());
        db.open();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mLocationRecyclerView = view.findViewById(R.id.recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Location> locations = LocationsHolder.get(getActivity()).getLocations();
        mAdapter = new LocationAdapter(locations);
        mLocationRecyclerView.setAdapter(mAdapter);

        Cursor c = db.getAllCities();
        if(c.moveToFirst()){
            do{
                Location loc = new Location();
                loc.setName(c.getString(1));
                mAdapter.mLocations.add(loc);
            }while(c.moveToNext());
        }

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
                        if (task != null){
                            long id = db.addCity(task);

                            Toast toast = Toast.makeText(getActivity(),
                                    "Location added",
                                    Toast.LENGTH_SHORT);
                            toast.show();

                            Location loc = new Location();
                            loc.setName(task);
                            mAdapter.mLocations.add(loc);
                            mAdapter.notifyItemInserted((int) id);
                        }
                    }
                })
                .setNegativeButton("Cancell", null)
                .create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                Toast toast = Toast.makeText(getActivity(),
                        "Add a location",
                        Toast.LENGTH_SHORT);
                toast.show();

                showAddItemDialog(getContext());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Holder

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTextView;
        private Location mLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            if(mLocation.getName().equals("GPS")){

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

                    LocationParams.Builder builder = new LocationParams.Builder()
                            .setAccuracy(LocationAccuracy.HIGH)
                            .setDistance(0)
                            .setInterval(5000);

                    SmartLocation.with(getActivity()).location().oneFix().config(builder.build())
                            .start(new OnLocationUpdatedListener() {
                                @Override
                                public void onLocationUpdated(android.location.Location location) {
                                    Log.i("GPS", "Location" + location);
                                    Toast toast = Toast.makeText(getActivity(),
                                            location.getLatitude() + " " + location.getLongitude(),
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                }


            }else{
                Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
                startActivity(intent);
            }
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
        }
    }

    // Adapter

    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> mLocations;

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
