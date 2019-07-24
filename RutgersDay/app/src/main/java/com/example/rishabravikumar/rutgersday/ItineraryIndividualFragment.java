package com.example.rishabravikumar.rutgersday;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.rishabravikumar.rutgersday.ProgramsCard;
import android.text.method.ScrollingMovementMethod;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.Button;
import android.support.v4.app.DialogFragment;

import org.w3c.dom.Text;

public class ItineraryIndividualFragment extends DialogFragment {

    MapView mMapView;
    TextView programTitle;
    TextView campus;
    TextView description;
    TextView time;
    TextView department;
    TextView programType;
    Button button;

    String _programTitle;
    String _campus;
    String _location;
    String _description;
    String _time;
    String _department;
    String _programType;
    String _SID;
    LatLng latLng;

    private GoogleMap googleMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_program_individual, container, false);
        programTitle = (TextView)rootView.findViewById(R.id.programTitle);
        campus = (TextView)rootView.findViewById(R.id.campus);
        description = (TextView)rootView.findViewById(R.id.description);
        time = (TextView)rootView.findViewById(R.id.time);
        department = (TextView)rootView.findViewById(R.id.department);
        programType = (TextView)rootView.findViewById(R.id.programType);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        button = (Button) rootView.findViewById(R.id.addButton);

        for(int i = 0; i < ((MainActivity) getActivity()).itineraryCards.size(); i++){
            if(((MainActivity) getActivity()).itineraryCards.get(i).getName().equalsIgnoreCase(_programTitle)){
                button.setText("Remove");
                break;
            }
        }

        programTitle.setText(_programTitle);
        campus.setText(_campus);
        description.setText(_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        if(_time.equals(" ") || _time == null) {
            time.setText("All Day");
        }else {
            time.setText(_time);
        }
        department.setText(_department);
        programType.setText(_programType);
        if(((MainActivity)getActivity()).itineraryFragment.isVisible()){
            button.setVisibility(View.GONE);
        }else{
            button.setVisibility(View.VISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.getText().toString().equalsIgnoreCase("add")) {
                    ((MainActivity) getActivity()).itineraryCards.add(new ProgramsCard(_programTitle, _time, _SID, _campus, 0, null));
                    button.setText("Remove");
                }
                else if(button.getText().toString().equalsIgnoreCase("remove")) {
                    for(int i = 0; i < ((MainActivity) getActivity()).itineraryCards.size(); i++){
                        if(((MainActivity) getActivity()).itineraryCards.get(i).getName().equalsIgnoreCase(_programTitle)){
                            ((MainActivity) getActivity()).itineraryCards.remove(i);
                            break;
                        }
                    }
                    button.setText("Add");
                }
            }
        });

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.FILL_HORIZONTAL;

        if(latLng.latitude == 0 && latLng.longitude == 0){

            mMapView.onCreate(savedInstanceState);

            mMapView.onResume();

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.setVisibility(View.GONE);
        }else {
            mMapView.setVisibility(View.VISIBLE);

            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                /*

                get string of latlng & info from string & populate in for loop

                String[] latlong =  "-34.8799074,174.7565664".split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                */


                    googleMap.addMarker(new MarkerOptions().position(latLng).title(_location));

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
