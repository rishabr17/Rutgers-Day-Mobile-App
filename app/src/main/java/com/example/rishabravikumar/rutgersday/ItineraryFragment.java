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
import com.viethoa.adapters.AlphabetAdapter;
import com.viethoa.models.AlphabetItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import ru.rambler.libs.swipe_layout.SwipeLayout;
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
import android.support.v4.app.DialogFragment;
import android.view.*;



public class ItineraryFragment extends DialogFragment {

    private SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    RecyclerView recyclerView;
    RecyclerViewFastScroller fastScroller;
    ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
    View view;
    boolean itiner = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_geo_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedcorners));
        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.FILL_HORIZONTAL;

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
        if(lots != null || programInfoList == null || itiner)
            fastScroller.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(15);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*1);
        getDialog().getWindow().setLayout(width, height);
        /*if(itiner) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.setMargins(35, 35, 35, 35);
        }*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ArrayList<MainActivity.ParkingLotInfo> lots = null;


    ArrayList<ProgramInfoInList> programInfoList = new ArrayList<>();


    List<ProgramsCard> buschprogramsList1 = new ArrayList<>();
    List<ProgramsCard> collegeaveprogramsList1 = new ArrayList<>();
    List<ProgramsCard> douglassprogramsList1 = new ArrayList<>();
    List<ProgramsCard> newarkprogramsList1 = new ArrayList<>();
    List<ProgramsCard> camdenprogramsList1 = new ArrayList<>();


    ArrayList<ProgramsCard> listItiner = new ArrayList<ProgramsCard>();
    public void getItinerary(ArrayList<ProgramsCard> programsList){
        itiner = true;

        programInfoList.add(new ProgramInfoInList("l", null, null, null));

        sectionAdapter.removeAllSections();

        Collections.sort(programsList, new Comparator<ProgramsCard>() {
            @Override
            public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                {
                    if (!object1.time.contains("m") && !object2.time.contains("m"))
                        return object1.getName().compareTo(object2.getName());
                    else if (object1.time.contains("m") && !object2.time.contains("m"))
                        return -1;
                    else if (!object1.time.contains("m") && object2.time.contains("m"))
                        return 1;
                    else {
                        String time1 = object1.time;
                        String time2 = object2.time;
                        int timer1 = 0;
                        int timer2 = 0;

                        if (time1.contains("am")) {
                            time1 = time1.replaceAll("[^\\d-]", "");
                            timer1 = Integer.parseInt(time1);
                        }
                        if (time2.contains("am")) {
                            time2 = time2.replaceAll("[^\\d-]", "");
                            timer2 = Integer.parseInt(time2);
                        }
                        if (time1.contains("pm")) {
                            time1 = time1.replaceAll("[^\\d-]", "");
                            timer1 = Integer.parseInt(time1) + 1200;
                        }
                        if (time2.contains("pm")) {
                            time2 = time2.replaceAll("[^\\d-]", "");
                            timer2 = Integer.parseInt(time2) + 1200;
                        }
                        if (timer1 > timer2) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            }
        });
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
        if(programsList.size() >= 1) {
            ArrayList<ProgramsCard> buschs = new ArrayList<>();
            ArrayList<ProgramsCard> colleges = new ArrayList<>();
            ArrayList<ProgramsCard> douglass = new ArrayList<>();
            ArrayList<ProgramsCard> camdens = new ArrayList<>();
            ArrayList<ProgramsCard> newarks = new ArrayList<>();

            for(int i = 0; i < programsList.size(); i++) {
                Log.d("pontifex",programsList.get(i).getJSON().toString());
                if(programsList.get(i).campus.contains("Busch")){
                    buschs.add(programsList.get(i));
                }
                if(programsList.get(i).campus.contains("College")){
                    colleges.add(programsList.get(i));
                }
                if(programsList.get(i).campus.contains("Douglass")){
                    douglass.add(programsList.get(i));
                }
                if(programsList.get(i).campus.contains("Camden")){
                    camdens.add(programsList.get(i));
                }
                if(programsList.get(i).campus.contains("Newark")){
                    newarks.add(programsList.get(i));
                }
            }
            if(!douglass.isEmpty())
                sectionAdapter.addSection(new ProgramsSection("Cook/Douglass", douglass));
            if(!colleges.isEmpty())
                sectionAdapter.addSection(new ProgramsSection("College Ave", colleges));
            if(!buschs.isEmpty())
                sectionAdapter.addSection(new ProgramsSection("Busch", buschs));
            if(!newarks.isEmpty())
                sectionAdapter.addSection(new ProgramsSection("Newark", newarks));
            if(!camdens.isEmpty())
                sectionAdapter.addSection(new ProgramsSection("Camden", camdens));
            listItiner = programsList;
        }

        sectionAdapter.mDataArray = programsList;

        if(programsList.size() < 1){
            sectionAdapter.addSection(new ProgramsSection("Added Programs Will Appear Here", programsList));
        }

    }

    ///////////////////////////////////////////////////////////////

    private class ProgramsSection extends StatelessSection {

        String title;
        List<ProgramsCard> list;

        ProgramsSection(String title, List<ProgramsCard> list) {
            super(new SectionParameters.Builder(R.layout.itineraryitems)
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

            if(list.get(position).time.contains("m")) {
                String strTime = list.get(position).time;
                itemHolder.distanceView.setText(strTime);
            }else {
                itemHolder.distanceView.setText("All Day");
            }
            itemHolder.distanceView.setTextColor(Color.argb(255,33,117,165));

            itemHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        ((MainActivity) getActivity()).setIndividualProgram(list.get(position).SID, true);
                        ((MainActivity) getActivity()).itineraryIndividualFragment._SID = list.get(position).SID;
                        ((MainActivity) getActivity()).itineraryIndividualFragment._description = " ";
                        ((MainActivity) getActivity()).itineraryIndividualFragment._programType = " ";
                        ((MainActivity) getActivity()).itineraryIndividualFragment._department = " ";
                        ((MainActivity) getActivity()).itineraryIndividualFragment._time = " ";
                        ((MainActivity) getActivity()).itineraryIndividualFragment._location = " ";
                        ((MainActivity) getActivity()).itineraryIndividualFragment._programTitle = " ";
                        ((MainActivity) getActivity()).itineraryIndividualFragment._programTitle = list.get(position).getName();
                        try {
                            if (((MainActivity) getActivity()).programInfos.latLng != null) {
                                ((MainActivity) getActivity()).itineraryIndividualFragment.latLng = ((MainActivity) getActivity()).programInfos.latLng;
                            } else {
                                ((MainActivity) getActivity()).itineraryIndividualFragment.latLng = new LatLng(0, 0);
                            }
                        }catch(Exception e){
                            ((MainActivity) getActivity()).itineraryIndividualFragment.latLng = new LatLng(0, 0);
                        }
                        if(((MainActivity) getActivity()).programInfos.location.isEmpty()) {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._location = "Not Available";
                            //((MainActivity) getActivity()).individualProgram.location.setText("Not Available");
                        }
                        else {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._location = ((MainActivity) getActivity()).programInfos.location;
                            //((MainActivity) getActivity()).individualProgram.location.setText(((MainActivity) getActivity()).programInfos.location);
                        }
                        if(((MainActivity) getActivity()).programInfos.department.isEmpty()) {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._department = "Not Available";
                        }
                        else {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._department = ((MainActivity) getActivity()).programInfos.department;
                        }
                        if(((MainActivity) getActivity()).programInfos.location.isEmpty()) {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._programType = "";
                        }
                        else {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._programType = ((MainActivity) getActivity()).programInfos.location;
                        }
                        if(((MainActivity)getActivity()).programInfos.time.equalsIgnoreCase("continuous")) {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._time = "All Day";
                            //((MainActivity) getActivity()).individualProgram.time.setText("All Day");
                        }
                        else if(!((MainActivity)getActivity()).programInfos.time.isEmpty()) {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._time = ((MainActivity) getActivity()).programInfos.time.substring(0, ((MainActivity) getActivity()).programInfos.time.length() - 4) + ":" + ((MainActivity) getActivity()).programInfos.time.substring(((MainActivity) getActivity()).programInfos.time.length() - 4, ((MainActivity) getActivity()).programInfos.time.length());
                            //((MainActivity) getActivity()).individualProgram.time.setText(((MainActivity) getActivity()).programInfos.time.substring(0, ((MainActivity) getActivity()).programInfos.time.length() - 4) + ":" + ((MainActivity) getActivity()).programInfos.time.substring(((MainActivity) getActivity()).programInfos.time.length() - 4, ((MainActivity) getActivity()).programInfos.time.length()));
                        }
                        else if(!((MainActivity)getActivity()).programInfos.campus.isEmpty()){
                            ((MainActivity) getActivity()).itineraryIndividualFragment._time = "All Day";
                            //((MainActivity) getActivity()).individualProgram.time.setText("Not Available");
                        }
                        else {
                            ((MainActivity) getActivity()).itineraryIndividualFragment._time = "Not Available";
                            //((MainActivity) getActivity()).individualProgram.time.setText("Not Available");
                        }

                        ((MainActivity) getActivity()).itineraryIndividualFragment._campus = ((MainActivity) getActivity()).programInfos.campus;

                    ((MainActivity) getActivity()).itineraryIndividualFragment._description = ((MainActivity) getActivity()).programInfos.programDescription;
                    if(((MainActivity)getActivity()).programInfos.campus.contains("Newark")){
                        ((MainActivity)getActivity()).itineraryIndividualFragment.latLng = new LatLng(40.742092,-74.174915);
                        ((MainActivity)getActivity()).itineraryIndividualFragment._programType = "Dana Library";
                    }
                    if(((MainActivity)getActivity()).programInfos.campus.contains("Camden")){
                        ((MainActivity)getActivity()).itineraryIndividualFragment.latLng = new LatLng(39.948664,-75.122630);
                        ((MainActivity)getActivity()).itineraryIndividualFragment._programType = "Campus Center";
                    }
                }
            });
            itemHolder.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                }

                @Override
                public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                    /*for(int i = 0; i < ((MainActivity) getActivity()).itineraryCards.size(); i++){
                        if(((MainActivity) getActivity()).itineraryCards.get(i).getName().equalsIgnoreCase(listItiner.get(position).getName())){
                            ((MainActivity) getActivity()).itineraryCards.remove(i);
                            break;
                        }
                    }*/
                    Log.d("positron",Integer.toString(position));
                    //sectionAdapter.removeAt(position, getActivity());
                    for(int i = 0; i < list.size(); i++){
                        if(list.get(i).name.equals(itemHolder.titleview.getText())){
                            list.remove(i);
                        }
                    }
                    for(int i = 0; i < ((MainActivity)getActivity()).itineraryCards.size(); i++){
                        if(((MainActivity)getActivity()).itineraryCards.get(i).name.equals(itemHolder.titleview.getText())){
                            ((MainActivity)getActivity()).itineraryCards.remove(i);
                        }
                    }
                    sectionAdapter.notifyDataSetChanged();
                    swipeLayout.reset();
                }

                @Override
                public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                    swipeLayout.reset();
                }

                @Override
                public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

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
            if(programInfoList == null && lots == null ) {
                headerHolder.titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                headerHolder.titleView.setPadding(0,10,0,0);
            }else{
                headerHolder.titleView.setPadding(25,0,0,0);
                //((ViewGroup.MarginLayoutParams)headerHolder.titleView.getLayoutParams()).setMargins(15,0,0,0);
            }

            if(itiner){
                headerHolder.titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
        private final SwipeLayout swipeLayout;
        private final CardView cardView;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            cardView = (CardView)view.findViewById(R.id.rootView);
            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipelayout);
            titleview = (TextView) view.findViewById(R.id.Title);
            distanceView = (TextView) view.findViewById(R.id.Distance);
            distanceView.setTextColor(Color.WHITE);
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