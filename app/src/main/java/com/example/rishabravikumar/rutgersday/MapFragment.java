package com.example.rishabravikumar.rutgersday;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.widget.Button;
import android.support.v7.widget.CardView;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    CardView button;
    CardView button2;
    CardView button3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            alertBuilder.setCancelable(false);
            alertBuilder.setTitle("Location Permission Necessary");
            alertBuilder.setMessage("Please allow this app to access your location, as the program information and other emergency communications will be based on this information.");
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
        button = (CardView) rootView.findViewById(R.id.button345);
        button2 = (CardView) rootView.findViewById(R.id.button34);
        button3 = (CardView) rootView.findViewById(R.id.button4);
        if(!((MainActivity)getActivity()).newBrunswick){
            button3.setVisibility(View.GONE);
        }
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
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

                LatLng newBruns = new LatLng(40.5008, -74.4474);
                LatLng newar = new LatLng(40.7412, -74.1753);
                LatLng camde = new LatLng(39.9485, -75.1219);
                final BitmapDescriptor parkingIcon = BitmapDescriptorFactory.fromResource(R.drawable.parkingicon);
                final BitmapDescriptor busIcon = BitmapDescriptorFactory.fromResource(R.drawable.busicon);

                for(int i = 0; i < ((MainActivity)getActivity()).lotsInfo.size(); i++) {
                    googleMap.addMarker(new MarkerOptions().icon(parkingIcon).position(((MainActivity) getActivity()).lotsInfo.get(i).latLng).title(((MainActivity)getActivity()).lotsInfo.get(i).title));
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        googleMap.clear();
                        for(int i = 0; i < ((MainActivity)getActivity()).itineraryCards.size(); i++) {
                            if(((MainActivity)getActivity()).itineraryCards.get(i).location != null)
                                googleMap.addMarker(new MarkerOptions().position(((MainActivity) getActivity()).itineraryCards.get(i).location).title(((MainActivity)getActivity()).itineraryCards.get(i).name));
                            else
                                continue;
                        }
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        googleMap.clear();
                        for(int i = 0; i < ((MainActivity)getActivity()).lotsInfo.size(); i++) {
                            googleMap.addMarker(new MarkerOptions().icon(parkingIcon).position(((MainActivity) getActivity()).lotsInfo.get(i).latLng).title(((MainActivity)getActivity()).lotsInfo.get(i).title));
                        }
                    }
                });

                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        googleMap.clear();
                        for(int i = 0; i < ((MainActivity)getActivity()).stopsInfo.size(); i++) {
                            googleMap.addMarker(new MarkerOptions().icon(busIcon).position(((MainActivity) getActivity()).stopsInfo.get(i).latLng).title(((MainActivity)getActivity()).stopsInfo.get(i).title));
                        }
                    }
                });

                if(((MainActivity)getActivity()).newBrunswick){
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(newBruns).zoom(13).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                else if(((MainActivity)getActivity()).newark) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(newar).zoom(15).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                else{
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(camde).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                if(((MainActivity)getActivity()).bottomNavigationView.getVisibility() == View.GONE){
                    ((MainActivity)getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

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
