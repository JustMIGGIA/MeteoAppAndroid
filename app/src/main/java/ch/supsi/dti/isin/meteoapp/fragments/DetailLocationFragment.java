package ch.supsi.dti.isin.meteoapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;

public class DetailLocationFragment extends Fragment {
    private static final String ARG_LOCATION_ID = "location_id";

    private Location mLocation;
    private TextView mNameTextView;
    private ImageView mImageView;
    private TextView mDescriptionTextView;
    private TextView mTempTextView;
    private TextView mMinMaxTempTextView;


    public static DetailLocationFragment newInstance(String locationId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION_ID, locationId);

        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String locationId = (String) getArguments().getSerializable(ARG_LOCATION_ID);
        mLocation = LocationsHolder.get(getActivity()).getLocation(locationId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_location, container, false);

        mNameTextView = v.findViewById(R.id.name_textView);
        mImageView = v.findViewById(R.id.image_detail);
        mDescriptionTextView = v.findViewById(R.id.description_textView);
        mTempTextView = v.findViewById(R.id.temp_textView);
        mMinMaxTempTextView = v.findViewById(R.id.min_max_temp_textView);

        mNameTextView.setText(mLocation.getName());
        int id = getContext().getResources().getIdentifier(mLocation.getWeather_icon(), "drawable", getContext().getPackageName());
        mImageView.setImageResource(id);
        mDescriptionTextView.setText(mLocation.getWeather_descr());
        mTempTextView.setText(mLocation.getTemp() + "°");
        mMinMaxTempTextView.setText(mLocation.getTemp_min() + "°" + " | " + mLocation.getTemp_max() + "°");

        return v;
    }
}

