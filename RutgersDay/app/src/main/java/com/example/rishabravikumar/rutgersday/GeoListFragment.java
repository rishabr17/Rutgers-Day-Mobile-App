package com.example.rishabravikumar.rutgersday;

import android.*;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import java.net.HttpURLConnection;

import com.google.android.gms.cast.framework.media.widget.MiniControllerFragment;
import com.viethoa.adapters.AlphabetAdapter;
import com.viethoa.models.AlphabetItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.webkit.WebView;
import android.util.Log;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeoutException;
import com.example.rishabravikumar.rutgersday.ProgramsCard;

import android.support.v4.view.ViewPager;
import com.example.rishabravikumar.rutgersday.ProgramInfoInList;

import com.google.android.gms.maps.model.LatLng;
import com.viethoa.RecyclerViewFastScroller;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import com.example.rishabravikumar.rutgersday.SectionParameters;
import com.example.rishabravikumar.rutgersday.SectionedRecyclerViewAdapter;
//import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import com.example.rishabravikumar.rutgersday.StatelessSection;
import android.os.Handler;
import android.widget.ProgressBar;
import java.net.URLDecoder;

public class GeoListFragment extends Fragment {

    private SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    RecyclerView recyclerView;
    RecyclerViewFastScroller fastScroller;
    ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
    View view;
    boolean itiner = false;
    double lats = 10;
    double longs = 10;
    boolean foodtrue = false;
    boolean interests = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_geo_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    default:
                        return 2;
                }
            }
        });
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(sectionAdapter);

        fastScroller = ((RecyclerViewFastScroller)view.findViewById(R.id.fast_scroller));
        fastScroller.setUpAlphabet(mAlphabetItems);
        fastScroller.setRecyclerView(recyclerView);
        fastScroller.setVisibility(View.VISIBLE);
        fastScroller.setBackgroundColor(Color.argb(150, 236, 240, 241));
        if(foodtrue ||  lots != null || stops != null || programInfoList == null || ((MainActivity)getActivity()).interestss)
            fastScroller.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(15);

        if(itiner) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.setMargins(25, 25, 25, 25);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ArrayList<MainActivity.ParkingLotInfo> lots = null;

    ////// CALL THIS WHEN INITIALIZING BUS STOPS LIST FRAGMENT //////
    public void getLots(double latitude, double longitude, boolean newBrunswick, boolean newark, boolean camden) {
        List<ProgramsCard> buschprogramsList = new ArrayList<>();
        List<ProgramsCard> collegeaveprogramsList = new ArrayList<>();
        List<ProgramsCard> douglassprogramsList = new ArrayList<>();
        List<ProgramsCard> camdenprogramsList = new ArrayList<>();
        List<ProgramsCard> newarkprogramsList = new ArrayList<>();
        for (MainActivity.ParkingLotInfo parkingLotInfo : lots) {
            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(parkingLotInfo.latLng.latitude);
            locationB.setLongitude(parkingLotInfo.latLng.longitude);
            lats = latitude;
            longs = longitude;
            float distance = locationA.distanceTo(locationB)/1000;
            if(newBrunswick) {
                if (parkingLotInfo.campus.contains("Busch")) {
                    buschprogramsList.add(new ProgramsCard(parkingLotInfo.title, null, null, (double) distance, parkingLotInfo.latLng));
                }
                if (parkingLotInfo.campus.contains("College")) {
                    collegeaveprogramsList.add(new ProgramsCard(parkingLotInfo.title, null, null, (double) distance, parkingLotInfo.latLng));
                }
                if (parkingLotInfo.campus.contains("Douglass")) {
                    douglassprogramsList.add(new ProgramsCard(parkingLotInfo.title, null, null, (double) distance, parkingLotInfo.latLng));
                }
            }
            else if(camden){
                if(parkingLotInfo.campus.contains("Camden")){
                    camdenprogramsList.add(new ProgramsCard(parkingLotInfo.title, null, null, (double) distance, parkingLotInfo.latLng));
                }
            }
            else if(newark){
                if(parkingLotInfo.campus.contains("Newark")){
                    newarkprogramsList.add(new ProgramsCard(parkingLotInfo.title, null, null, (double) distance, parkingLotInfo.latLng));
                }
            }
        }
        sectionAdapter.removeAllSections();
        if(newBrunswick) {
            sectionAdapter.addSection(new ProgramsSection("Cook/Douglass", douglassprogramsList));
            sectionAdapter.addSection(new ProgramsSection("College Ave", collegeaveprogramsList));
            sectionAdapter.addSection(new ProgramsSection("Busch", buschprogramsList));
        }else if(camden){
            sectionAdapter.addSection(new ProgramsSection("Parking", camdenprogramsList));
        }else if(newark){
            sectionAdapter.addSection(new ProgramsSection("Parking", newarkprogramsList));
        }
    }

    public ArrayList<MainActivity.ParkingLotInfo> stops = null;

    ////// CALL THIS WHEN INITIALIZING BUS STOPS LIST FRAGMENT //////
    public void getStops(double latitude, double longitude) {
        List<ProgramsCard> buschprogramsList = new ArrayList<>();
        List<ProgramsCard> collegeaveprogramsList = new ArrayList<>();
        List<ProgramsCard> douglassprogramsList = new ArrayList<>();
        for (MainActivity.ParkingLotInfo parkingLotInfo : stops) {
            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(parkingLotInfo.latLng.latitude);
            locationB.setLongitude(parkingLotInfo.latLng.longitude);
            lats = latitude;
            longs = longitude;
            float distance = locationA.distanceTo(locationB)/1000;
            if(parkingLotInfo.campus.contains("Busch")){
                buschprogramsList.add(new ProgramsCard(parkingLotInfo.title, null,null, (double)distance, parkingLotInfo.latLng));
            }
            if(parkingLotInfo.campus.contains("College")){
                collegeaveprogramsList.add(new ProgramsCard(parkingLotInfo.title, null,null, (double)distance, parkingLotInfo.latLng));
            }
            if(parkingLotInfo.campus.contains("Douglass")){
                douglassprogramsList.add(new ProgramsCard(parkingLotInfo.title, null,null, (double)distance, parkingLotInfo.latLng));
            }
        }
        sectionAdapter.removeAllSections();
        sectionAdapter.addSection(new ProgramsSection("Cook/Douglass", douglassprogramsList));
        sectionAdapter.addSection(new ProgramsSection("College Ave", collegeaveprogramsList));
        sectionAdapter.addSection(new ProgramsSection("Busch", buschprogramsList));
    }


    ArrayList<GeoListFragment.ParkingLotInfo> foodInfoList = new ArrayList<>();
    ///// CALL THIS WHEN INITIALIZING PROGRAMS LIST FRAGMENT ///////
    public void getFoods(final int code) {

        final Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL programURL = null;
                HttpURLConnection programConnection = null;
                InputStream programStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                try {
                    programURL = new URL("https://api.rutgersday.rutgers.edu/lists/"+Integer.toString(code));
                    programConnection = (HttpURLConnection) programURL.openConnection();
                    programConnection.setConnectTimeout(1500);
                    programConnection.setRequestProperty("X-Authorized-Token","9a5de304-73fd-4a1c-a326-ca710d777cef");
                    try {
                        programStream = programConnection.getInputStream();
                    }catch(Exception e){
                        foodInfoList = null;
                        Thread.currentThread().interrupt();}
                    inputStreamReader = new InputStreamReader(programStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    StringBuilder sb = new StringBuilder();
                    while (line != null) {
                        sb.append(line);
                        line = bufferedReader.readLine();
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    foodInfoList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        // WHEN THE FOODS LIST GETS POPULATED FILL THIS
                        foodInfoList.add(new ParkingLotInfo(jsonArray.getJSONObject(i).getString("sid"), jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("time"), "0", new LatLng(Double.parseDouble(jsonArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jsonArray.getJSONObject(i).getString("longitude"))) ));

                    }
                }catch(Exception e){}
            }
        });
        internetThread.start();
        try {
            internetThread.join();
        }catch(Exception e){}
        sectionAdapter.removeAllSections();

        List<ProgramsCard> programsList = new ArrayList<>();
        if(programInfoList == null){
            sectionAdapter.addSection(new ProgramsSection("Sorry, Unable To Connect To Server", programsList));
        }else {

            for (ProgramInfoInList programInfo : programInfoList) {
                programsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, 0, null));
            }
            Collections.sort(programsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            if(foodtrue) {
                sectionAdapter.addSection(new ProgramsSection("Food Vendors", programsList));
            }else {
                sectionAdapter.addSection(new ProgramsSection("Programs", programsList));
            }
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < programsList.size(); i++) {
                String name = programsList.get(i).getName();
                if (name == null || name.trim().isEmpty())
                    continue;

                String word = name.substring(0, 1);
                if (!strAlphabets.contains(word)) {
                    strAlphabets.add(word);
                    mAlphabetItems.add(new AlphabetItem(i, word, false));
                }
            }
            sectionAdapter.mDataArray = programsList;
        }
    }


    ArrayList<ProgramInfoInList> programInfoList = new ArrayList<>();






    public void getItinerary(ArrayList<ProgramsCard> programsList){
        itiner = true;



        programInfoList.add(new ProgramInfoInList("l", null, null, null));

        sectionAdapter.removeAllSections();

        Collections.sort(programsList, new Comparator<ProgramsCard>() {
            @Override
            public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                return object1.getName().compareTo(object2.getName());
            }
        });
        sectionAdapter.addSection(new ProgramsSection("Itinerary", programsList));
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < programsList.size(); i++) {
            String name = programsList.get(i).getName();
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }

        sectionAdapter.mDataArray = programsList;
    }


    ///// CALL THIS WHEN INITIALIZING PROGRAMS LIST FRAGMENT ///////
    public void getPrograms(double latitude, double longitude, String listsCode, View loader) {
        final String listCode = listsCode;

        final Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL programURL = null;
                HttpURLConnection programConnection = null;
                InputStream programStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                try {
                    if(listCode.equals("bigR") || listCode.equals("scarletstage")){
                        programURL = new URL("https://api.rutgersday.rutgers.edu/" + listCode);
                    }else {
                        programURL = new URL("https://api.rutgersday.rutgers.edu/lists/" + listCode);
                    }
                    programConnection = (HttpURLConnection) programURL.openConnection();
                    programConnection.setConnectTimeout(1500);
                    programConnection.setRequestProperty("X-Authorized-Token","9a5de304-73fd-4a1c-a326-ca710d777cef");
                    try {
                        programStream = programConnection.getInputStream();
                    }catch(Exception e){
                        programInfoList = null;
                        Thread.currentThread().interrupt();}
                    inputStreamReader = new InputStreamReader(programStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    StringBuilder sb = new StringBuilder();
                    while (line != null) {
                        sb.append(line);
                        line = bufferedReader.readLine();
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    programInfoList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.d("pontifex", jsonArray.toString());
                        programInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(i).getString("campus"), jsonArray.getJSONObject(i).getString("sid"), jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                    }
                }catch(Exception e){}
            }
        });
        internetThread.start();
        try {
            internetThread.join();

        }catch(Exception e){}

        sectionAdapter.removeAllSections();

        List<ProgramsCard> programsList = new ArrayList<>();
        if(programInfoList == null){
            sectionAdapter.addSection(new ProgramsSection("Sorry, Unable To Connect To Server", programsList));
        }else {

        for (ProgramInfoInList programInfo : programInfoList) {
            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(programInfo.latLng.latitude);
            locationB.setLongitude(programInfo.latLng.longitude);
            float distance = locationA.distanceTo(locationB)/1000;
            programsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
        }
        Collections.sort(programsList, new Comparator<ProgramsCard>() {
            @Override
            public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                return object1.getName().compareTo(object2.getName());
            }
        });
            if(listCode.contains("76")) {
                sectionAdapter.addSection(new ProgramsSection("Food Vendors", programsList));
            }else {
                sectionAdapter.addSection(new ProgramsSection("Programs", programsList));
            }
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < programsList.size(); i++) {
                String name = programsList.get(i).getName();
                if (name == null || name.trim().isEmpty())
                    continue;

                String word = name.substring(0, 1);
                if (!strAlphabets.contains(word)) {
                    strAlphabets.add(word);
                    mAlphabetItems.add(new AlphabetItem(i, word, false));
                }
            }
            sectionAdapter.mDataArray = programsList;
        }

    }


    List<ProgramInfoInList> buschProgramInfoList = new ArrayList<>();
    List<ProgramInfoInList> collegeAveProgramInfoList = new ArrayList<>();
    List<ProgramInfoInList> douglassProgramInfoList = new ArrayList<>();
    public void getFoodsPrograms(double latitude, double longitude, String listsCode, View loader) {
        final String listCode = listsCode;
        if(fastScroller != null) {
            fastScroller = ((RecyclerViewFastScroller) view.findViewById(R.id.fast_scroller));
            fastScroller.setVisibility(View.GONE);
        }
        final Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL programURL = null;
                HttpURLConnection programConnection = null;
                InputStream programStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                buschProgramInfoList.clear();
                collegeAveProgramInfoList.clear();
                douglassProgramInfoList.clear();
                for(int i = 1; i<4; i++) {
                    try {
                        if(i == 1)
                            programURL = new URL("https://api.rutgersday.rutgers.edu/lists/l8p76");
                        if(i == 2)
                            programURL = new URL("https://api.rutgersday.rutgers.edu/lists/l6p76");
                        if(i == 3)
                            programURL = new URL("https://api.rutgersday.rutgers.edu/lists/l5p76");
                        programConnection = (HttpURLConnection) programURL.openConnection();
                        programConnection.setConnectTimeout(1500);
                        programConnection.setRequestProperty("X-Authorized-Token", "9a5de304-73fd-4a1c-a326-ca710d777cef");
                        try {
                            programStream = programConnection.getInputStream();
                        } catch (Exception e) {
                            buschProgramInfoList = null;
                            collegeAveProgramInfoList = null;
                            douglassProgramInfoList = null;
                            Thread.currentThread().interrupt();
                        }
                        inputStreamReader = new InputStreamReader(programStream);
                        bufferedReader = new BufferedReader(inputStreamReader);
                        String line = bufferedReader.readLine();
                        StringBuilder sb = new StringBuilder();
                        while (line != null) {
                            sb.append(line);
                            line = bufferedReader.readLine();
                        }
                        JSONArray jsonArray = new JSONArray(sb.toString());
                        for (int x = 0; x < jsonArray.length(); x++) {
                            if(i == 1)
                                buschProgramInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(x).getString("sid"), jsonArray.getJSONObject(x).getString("name"), jsonArray.getJSONObject(x).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                            if(i == 2)
                                collegeAveProgramInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(x).getString("sid"), jsonArray.getJSONObject(x).getString("name"), jsonArray.getJSONObject(x).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                            if(i == 3)
                                douglassProgramInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(x).getString("sid"), jsonArray.getJSONObject(x).getString("name"), jsonArray.getJSONObject(x).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        internetThread.start();
        try {
            internetThread.join();

        }catch(Exception e){}

        sectionAdapter.removeAllSections();

        List<ProgramsCard> buschprogramsList = new ArrayList<>();
        List<ProgramsCard> collegeaveprogramsList = new ArrayList<>();
        List<ProgramsCard> douglassprogramsList = new ArrayList<>();
        if(programInfoList == null){
            sectionAdapter.addSection(new ProgramsSection("Sorry, Unable To Connect To Server", buschprogramsList));
        }else {

            for (ProgramInfoInList programInfo : buschProgramInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                buschprogramsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
            }
            Collections.sort(buschprogramsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            for (ProgramInfoInList programInfo : collegeAveProgramInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                collegeaveprogramsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
            }
            Collections.sort(collegeaveprogramsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            for (ProgramInfoInList programInfo : douglassProgramInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                douglassprogramsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
            }
            Collections.sort(douglassprogramsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });

            sectionAdapter.addSection(new ProgramsSection("Cook/Douglass", douglassprogramsList));
            sectionAdapter.addSection(new ProgramsSection("College Ave", collegeaveprogramsList));
            sectionAdapter.addSection(new ProgramsSection("Busch", buschprogramsList));

        }


    }
    public void getInterestPrograms(double latitude, double longitude, String listsCode, View loader) {
        final String listCode = listsCode;
        if(fastScroller != null) {
            fastScroller = ((RecyclerViewFastScroller) view.findViewById(R.id.fast_scroller));
            fastScroller.setVisibility(View.GONE);
        }
        final Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL programURL = null;
                HttpURLConnection programConnection = null;
                InputStream programStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                buschProgramInfoList.clear();
                collegeAveProgramInfoList.clear();
                douglassProgramInfoList.clear();
                    try {
                        programURL = new URL("https://api.rutgersday.rutgers.edu/lists/"+listCode);
                        programConnection = (HttpURLConnection) programURL.openConnection();
                        programConnection.setConnectTimeout(1500);
                        programConnection.setRequestProperty("X-Authorized-Token", "9a5de304-73fd-4a1c-a326-ca710d777cef");
                        try {
                            programStream = programConnection.getInputStream();
                        } catch (Exception e) {
                            buschProgramInfoList = null;
                            collegeAveProgramInfoList = null;
                            douglassProgramInfoList = null;
                            Thread.currentThread().interrupt();
                        }
                        inputStreamReader = new InputStreamReader(programStream);
                        bufferedReader = new BufferedReader(inputStreamReader);
                        String line = bufferedReader.readLine();
                        StringBuilder sb = new StringBuilder();
                        while (line != null) {
                            sb.append(line);
                            line = bufferedReader.readLine();
                        }
                        JSONArray jsonArray = new JSONArray(sb.toString());
                        for (int x = 0; x < jsonArray.length(); x++) {
                            if(Integer.parseInt(jsonArray.getJSONObject(x).getString("campus")) == 8)
                                buschProgramInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(x).getString("sid"), jsonArray.getJSONObject(x).getString("name"), jsonArray.getJSONObject(x).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                            if(Integer.parseInt(jsonArray.getJSONObject(x).getString("campus")) == 6)
                                collegeAveProgramInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(x).getString("sid"), jsonArray.getJSONObject(x).getString("name"), jsonArray.getJSONObject(x).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                            if(Integer.parseInt(jsonArray.getJSONObject(x).getString("campus")) == 5)
                                douglassProgramInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(x).getString("sid"), jsonArray.getJSONObject(x).getString("name"), jsonArray.getJSONObject(x).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                        }
                    } catch (Exception e) {
                    }
            }
        });
        internetThread.start();
        try {
            internetThread.join();

        }catch(Exception e){}

        sectionAdapter.removeAllSections();

        List<ProgramsCard> buschprogramsList = new ArrayList<>();
        List<ProgramsCard> collegeaveprogramsList = new ArrayList<>();
        List<ProgramsCard> douglassprogramsList = new ArrayList<>();
        if(programInfoList == null){
            sectionAdapter.addSection(new ProgramsSection("Sorry, Unable To Connect To Server", buschprogramsList));
        }else {

            for (ProgramInfoInList programInfo : buschProgramInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                buschprogramsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
            }
            Collections.sort(buschprogramsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            for (ProgramInfoInList programInfo : collegeAveProgramInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                collegeaveprogramsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
            }
            Collections.sort(collegeaveprogramsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
            for (ProgramInfoInList programInfo : douglassProgramInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                douglassprogramsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
            }
            Collections.sort(douglassprogramsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });

            sectionAdapter.addSection(new ProgramsSection("Cook/Douglass", douglassprogramsList));
            sectionAdapter.addSection(new ProgramsSection("College Ave", collegeaveprogramsList));
            sectionAdapter.addSection(new ProgramsSection("Busch", buschprogramsList));

        }


    }
    ///////////////////////////////////////////////////////////////

    private class ProgramsSection extends StatelessSection {

        String title;
        List<ProgramsCard> list;

        ProgramsSection(String title, List<ProgramsCard> list) {
            super(new SectionParameters.Builder(R.layout.geolistitems)
                    .headerResourceId(R.layout.generalinfoheaders)
                    .build());
            this.title = title;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            String name = list.get(position).getName();
            itemHolder.titleview.setText(name);

            if(lots != null) {
                double drawableInt = list.get(position).getMiles();
                DecimalFormat df = new DecimalFormat("##.#");
                itemHolder.distanceView.setVisibility(View.VISIBLE);
                if (df.format(drawableInt).contains("."))
                    itemHolder.distanceView.setText(" " + df.format(drawableInt) + " mi ");
                else
                    itemHolder.distanceView.setText(" " + df.format(drawableInt) + ".0 mi ");
                if(lats == 0 && longs == 0){
                    itemHolder.distanceView.setVisibility(View.GONE);
                }
                else {
                    if (drawableInt < 3) {
                        itemHolder.distanceView.getBackground().setColorFilter(Color.argb(255, 22, 160, 133), PorterDuff.Mode.MULTIPLY);
                    } else if (drawableInt < 5) {
                        itemHolder.distanceView.getBackground().setColorFilter(Color.argb(255, 243, 156, 18), PorterDuff.Mode.MULTIPLY);
                    } else {
                        itemHolder.distanceView.getBackground().setColorFilter(Color.argb(255, 231, 76, 60), PorterDuff.Mode.MULTIPLY);
                    }
                }
            }
            else if (stops != null) {
                double drawableInt = list.get(position).getMiles();
                DecimalFormat df = new DecimalFormat("##.#");
                itemHolder.distanceView.setVisibility(View.VISIBLE);
                if (df.format(drawableInt).contains("."))
                    itemHolder.distanceView.setText(" " + df.format(drawableInt) + " mi ");
                else
                    itemHolder.distanceView.setText(" " + df.format(drawableInt) + ".0 mi ");
                if(lats == 0 && longs == 0){
                    itemHolder.distanceView.setVisibility(View.GONE);
                }
                else {
                    if (drawableInt < 3) {
                        itemHolder.distanceView.getBackground().setColorFilter(Color.argb(255, 22, 160, 133), PorterDuff.Mode.MULTIPLY);
                    } else if (drawableInt < 5) {
                        itemHolder.distanceView.getBackground().setColorFilter(Color.argb(255, 243, 156, 18), PorterDuff.Mode.MULTIPLY);
                    } else {
                        itemHolder.distanceView.getBackground().setColorFilter(Color.argb(255, 231, 76, 60), PorterDuff.Mode.MULTIPLY);
                    }
                }
            }
            else{

                if(itiner){
                    itemHolder.distanceView.setText(list.get(position).time);
                }
                else if(list.get(position).time.isEmpty()) {
                    itemHolder.distanceView.setText("All Day");
                }else if(list.get(position).time.equalsIgnoreCase("continuous")){
                        itemHolder.distanceView.setText("All Day");
                }else {
                    String strTime = list.get(position).time;
                    itemHolder.distanceView.setText(strTime.substring(0,strTime.length()-4)+":"+strTime.substring(strTime.length()-4,strTime.length()));
                }
                itemHolder.distanceView.setTextColor(Color.argb(255,33,117,165));
            }

            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lots != null) {
                        LatLng lotLocation = list.get(position).location;
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lotLocation.latitude + "," + lotLocation.longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    else if(stops != null) {
                        LatLng lotLocation = list.get(position).location;
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lotLocation.latitude + "," + lotLocation.longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    else if(foodtrue && !((MainActivity)getActivity()).isFoodSelected){
                        ((MainActivity)getActivity()).isFoodSelected = true;
                        ((MainActivity) getActivity()).setIndividualProgram(list.get(position).SID, false);
                        ((MainActivity) getActivity()).findividualProgram._SID = list.get(position).SID;
                        ((MainActivity) getActivity()).findividualProgram._description = " ";
                        ((MainActivity) getActivity()).findividualProgram._programType = " ";
                        ((MainActivity) getActivity()).findividualProgram._department = " ";
                        ((MainActivity) getActivity()).findividualProgram._time = " ";
                        ((MainActivity) getActivity()).findividualProgram._location = " ";
                        ((MainActivity) getActivity()).findividualProgram._programTitle = " ";
                        ((MainActivity) getActivity()).findividualProgram._programTitle = list.get(position).getName();
                        try {
                            if (((MainActivity) getActivity()).programInfos.latLng != null) {
                                ((MainActivity) getActivity()).findividualProgram.latLng = ((MainActivity) getActivity()).programInfos.latLng;
                            } else {
                                ((MainActivity) getActivity()).findividualProgram.latLng = new LatLng(0, 0);
                            }
                        }catch(Exception e){
                            ((MainActivity) getActivity()).findividualProgram.latLng = new LatLng(0, 0);
                        }
                        if(((MainActivity) getActivity()).programInfos.location.isEmpty()) {
                            ((MainActivity) getActivity()).findividualProgram._location = "";
                            //((MainActivity) getActivity()).individualProgram.location.setText("Not Available");
                        }
                        else {
                            ((MainActivity) getActivity()).findividualProgram._location = ((MainActivity) getActivity()).programInfos.location;
                            //((MainActivity) getActivity()).individualProgram.location.setText(((MainActivity) getActivity()).programInfos.location);
                        }
                        if(((MainActivity) getActivity()).programInfos.department.isEmpty()) {
                            ((MainActivity) getActivity()).findividualProgram._department = "Not Available";
                        }
                        else {
                            ((MainActivity) getActivity()).findividualProgram._department = ((MainActivity) getActivity()).programInfos.department;
                        }
                        if(((MainActivity) getActivity()).programInfos.location.isEmpty()) {
                            ((MainActivity) getActivity()).findividualProgram._programType = "";
                        }
                        else {
                            ((MainActivity) getActivity()).findividualProgram._programType = ((MainActivity) getActivity()).programInfos.location;
                        }
                        if(((MainActivity)getActivity()).programInfos.time.equalsIgnoreCase("continuous")) {
                            ((MainActivity) getActivity()).findividualProgram._time = "All Day";
                            //((MainActivity) getActivity()).individualProgram.time.setText("All Day");
                        }
                        else if(!((MainActivity)getActivity()).programInfos.time.isEmpty()) {
                            ((MainActivity) getActivity()).findividualProgram._time = ((MainActivity) getActivity()).programInfos.time.substring(0, ((MainActivity) getActivity()).programInfos.time.length() - 4) + ":" + ((MainActivity) getActivity()).programInfos.time.substring(((MainActivity) getActivity()).programInfos.time.length() - 4, ((MainActivity) getActivity()).programInfos.time.length());
                            //((MainActivity) getActivity()).individualProgram.time.setText(((MainActivity) getActivity()).programInfos.time.substring(0, ((MainActivity) getActivity()).programInfos.time.length() - 4) + ":" + ((MainActivity) getActivity()).programInfos.time.substring(((MainActivity) getActivity()).programInfos.time.length() - 4, ((MainActivity) getActivity()).programInfos.time.length()));
                        }
                        else {
                            ((MainActivity) getActivity()).findividualProgram._time = "All Day";
                            //((MainActivity) getActivity()).individualProgram.time.setText("Not Available");
                        }


                        ((MainActivity) getActivity()).findividualProgram._campus = ((MainActivity) getActivity()).programInfos.campus;
                        try {
                            ((MainActivity) getActivity()).findividualProgram._description = URLDecoder.decode(((MainActivity) getActivity()).programInfos.programDescription, "UTF-8");
                        }catch(Exception e){

                            ((MainActivity) getActivity()).findividualProgram._description = ((MainActivity) getActivity()).programInfos.programDescription;
                        }
                        if(((MainActivity)getActivity()).newark){
                            ((MainActivity)getActivity()).findividualProgram.latLng = new LatLng(40.742092,-74.174915);
                            ((MainActivity)getActivity()).findividualProgram._programType = "Dana Library";
                        }
                        if(((MainActivity)getActivity()).camden){
                            ((MainActivity)getActivity()).findividualProgram.latLng = new LatLng(39.948664,-75.122630);
                            ((MainActivity)getActivity()).findividualProgram._programType = "Campus Center";
                        }
                    }
                    else if(!((MainActivity)getActivity()).isItemSelected) {
                        ((MainActivity)getActivity()).isItemSelected = true;
                        ((MainActivity) getActivity()).setIndividualProgram(list.get(position).SID, false);
                        ((MainActivity) getActivity()).individualProgram._SID = list.get(position).SID;
                        ((MainActivity) getActivity()).individualProgram._description = " ";
                        ((MainActivity) getActivity()).individualProgram._programType = " ";
                        ((MainActivity) getActivity()).individualProgram._department = " ";
                        ((MainActivity) getActivity()).individualProgram._time = " ";
                        ((MainActivity) getActivity()).individualProgram._location = " ";
                        ((MainActivity) getActivity()).individualProgram._programTitle = " ";
                        ((MainActivity) getActivity()).individualProgram._programTitle = list.get(position).getName();
                        try {
                            if (((MainActivity) getActivity()).programInfos.latLng != null) {
                                ((MainActivity) getActivity()).individualProgram.latLng = ((MainActivity) getActivity()).programInfos.latLng;
                            } else {
                                ((MainActivity) getActivity()).individualProgram.latLng = new LatLng(0, 0);
                            }
                        }catch(Exception e){
                            ((MainActivity) getActivity()).individualProgram.latLng = new LatLng(0, 0);
                        }
                        if(((MainActivity) getActivity()).programInfos.location.isEmpty()) {
                            ((MainActivity) getActivity()).individualProgram._location = "";
                            //((MainActivity) getActivity()).individualProgram.location.setText("Not Available");
                        }
                        else {
                            ((MainActivity) getActivity()).individualProgram._location = ((MainActivity) getActivity()).programInfos.location;
                            //((MainActivity) getActivity()).individualProgram.location.setText(((MainActivity) getActivity()).programInfos.location);
                        }
                        if(((MainActivity) getActivity()).programInfos.department.isEmpty()) {
                            ((MainActivity) getActivity()).individualProgram._department = "Not Available";
                        }
                        else {
                            ((MainActivity) getActivity()).individualProgram._department = ((MainActivity) getActivity()).programInfos.department;
                        }
                        if(((MainActivity) getActivity()).programInfos.location.isEmpty()) {
                            ((MainActivity) getActivity()).individualProgram._programType = "";
                        }
                        else {
                            ((MainActivity) getActivity()).individualProgram._programType = ((MainActivity) getActivity()).programInfos.location;
                        }
                        if(((MainActivity)getActivity()).programInfos.time.equalsIgnoreCase("continuous")) {
                            ((MainActivity) getActivity()).individualProgram._time = "All Day";
                            //((MainActivity) getActivity()).individualProgram.time.setText("All Day");
                        }
                        else if(!((MainActivity)getActivity()).programInfos.time.isEmpty()) {
                            ((MainActivity) getActivity()).individualProgram._time = ((MainActivity) getActivity()).programInfos.time.substring(0, ((MainActivity) getActivity()).programInfos.time.length() - 4) + ":" + ((MainActivity) getActivity()).programInfos.time.substring(((MainActivity) getActivity()).programInfos.time.length() - 4, ((MainActivity) getActivity()).programInfos.time.length());
                            //((MainActivity) getActivity()).individualProgram.time.setText(((MainActivity) getActivity()).programInfos.time.substring(0, ((MainActivity) getActivity()).programInfos.time.length() - 4) + ":" + ((MainActivity) getActivity()).programInfos.time.substring(((MainActivity) getActivity()).programInfos.time.length() - 4, ((MainActivity) getActivity()).programInfos.time.length()));
                        }
                        else {
                            ((MainActivity) getActivity()).individualProgram._time = "All Day";
                            //((MainActivity) getActivity()).individualProgram.time.setText("Not Available");
                        }

                        ((MainActivity) getActivity()).individualProgram._campus = ((MainActivity) getActivity()).programInfos.campus;
                        try {
                            ((MainActivity) getActivity()).individualProgram._description = URLDecoder.decode(((MainActivity) getActivity()).programInfos.programDescription, "UTF-8");
                        }catch(Exception e){

                            ((MainActivity) getActivity()).individualProgram._description = ((MainActivity) getActivity()).programInfos.programDescription;
                        }
                        if(((MainActivity)getActivity()).newark){
                            ((MainActivity)getActivity()).individualProgram.latLng = new LatLng(40.742092,-74.174915);
                            ((MainActivity)getActivity()).individualProgram._programType = "Dana Library";
                        }
                        if(((MainActivity)getActivity()).camden){
                            ((MainActivity)getActivity()).individualProgram.latLng = new LatLng(39.948664,-75.122630);
                            ((MainActivity)getActivity()).individualProgram._programType = "Campus Center";
                        }
                    }
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            if(programInfoList == null && lots == null && stops == null) {
                headerHolder.titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                headerHolder.titleView.setPadding(0,10,0,0);
            }else{
                headerHolder.titleView.setPadding(25,0,0,0);
                //((ViewGroup.MarginLayoutParams)headerHolder.titleView.getLayoutParams()).setMargins(15,0,0,0);
            }
            headerHolder.titleView.setText(title);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;

        HeaderViewHolder(View view) {
            super(view);

            titleView = (TextView) view.findViewById(R.id.programTitle);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView titleview;
        private final TextView distanceView;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            titleview = (TextView) view.findViewById(R.id.Title);
            distanceView = (TextView) view.findViewById(R.id.Distance);
            distanceView.setTextColor(Color.WHITE);
            if(distanceView != null)
            distanceView.setBackground(getResources().getDrawable(R.drawable.myborder));
        }
    }

    public class ParkingLotInfo {
        String title;
        String description;
        String campus;
        String location;
        LatLng latLng;
        public ParkingLotInfo(String title, String description, String campus, String location, LatLng latLng){
            this.title = title;
            this.description = description;
            this.campus = campus;
            this.location = location;
            this.latLng = latLng;
        }
    }
}