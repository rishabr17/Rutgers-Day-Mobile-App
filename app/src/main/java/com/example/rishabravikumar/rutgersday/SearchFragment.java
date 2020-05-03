package com.example.rishabravikumar.rutgersday;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import com.example.rishabravikumar.rutgersday.GeoListFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.content.Context;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.os.StrictMode;

import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.viethoa.models.AlphabetItem;

import org.json.JSONArray;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SearchView mSearchView;
    private ListView mListView;
    public String searchText = "";
    public customAdapter adapter;
    public ArrayList<ProgramsCard> fDataSet = new ArrayList<ProgramsCard>();
    public SearchFragment webTable;
    public ArrayList<ProgramsCard> programsList;

    public SearchFragment() {

    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        webTable = this;

        mListView = (ListView) view.findViewById(R.id.WebsiteTable);

        adapter = new customAdapter(getContext(), R.layout.geolistitems, programsList);
        fDataSet = programsList;
        mListView.setAdapter(adapter);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        //mListView.setTextFilterEnabled(true);
        setupSearchView();

        return view;
    }

    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconifiedByDefault(true);
    }

    ArrayList<ProgramInfoInList> programInfoList = new ArrayList<>();
    ///// CALL THIS WHEN INITIALIZING PROGRAMS LIST FRAGMENT ///////
    public void getPrograms(double latitude, double longitude) {

        final Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL programURL = null;
                HttpURLConnection programConnection = null;
                InputStream programStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                try {
                    programURL = new URL("https://api.rutgersday.rutgers.edu/lists/l0");
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
                        programInfoList.add(new ProgramInfoInList(jsonArray.getJSONObject(i).getString("sid"), jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("time"), new LatLng(Double.parseDouble(Integer.toString(41)/*jsonArray.getJSONObject(i).getString("latitude")*/), Double.parseDouble("-74.3"/*jsonArray.getJSONObject(i).getString("longitude")*/))));
                    }
                }catch(Exception e){
                }
            }
        });
        internetThread.start();
        try {
            internetThread.join();
        }catch(Exception e){}
        programsList = new ArrayList<>();
        if(programInfoList == null){
            // UNABLE TO CONNECT TO SERVER, DO SOMETHING
        }else {
            int i = 0;
            for (ProgramInfoInList programInfo : programInfoList) {
                Location locationA = new Location("point A");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(programInfo.latLng.latitude);
                locationB.setLongitude(programInfo.latLng.longitude);
                float distance = locationA.distanceTo(locationB)/1000;
                programsList.add(new ProgramsCard(programInfo.title, programInfo.duration, programInfo.SID, (double)distance, programInfo.latLng));
                //adapter.add(programsList.get(i));
                i++;
            }
            Collections.sort(programsList, new Comparator<ProgramsCard>() {
                @Override
                public int compare(final ProgramsCard object1, final ProgramsCard object2) {
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
    }

    //search functionality

    @Override
    public boolean onQueryTextChange(String newText){
        ArrayList<ProgramsCard> programsCardArrayList = new ArrayList<>(programsList);
        searchText = newText;
        for (int i = 0; i < programsCardArrayList.size(); i++) {
            if (!programsCardArrayList.get(i).name.toLowerCase().contains(searchText.toLowerCase())) {
                programsCardArrayList.remove(i);
                i--;
            }
        }
        adapter = new customAdapter(getContext(), R.layout.geolistitems, programsCardArrayList);
        mListView.setAdapter(adapter);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private class customAdapter extends ArrayAdapter<ProgramsCard> {
        public View v;
        public List<ProgramsCard> programsCards;

        public customAdapter(Context context, int resource, List<ProgramsCard> programsCards){
            super(context, resource, programsCards);
            this.programsCards = programsCards;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent){
            this.v = convertView;
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            v = inflater.inflate(R.layout.geolistitems, parent, false);

            ((TextView) v.findViewById(R.id.Title)).setText(programsCards.get(pos).name);
            if(programsCards.get(pos).time.equalsIgnoreCase("continuous"))
                ((TextView) v.findViewById(R.id.Distance)).setText("All Day");
            else if(!programsCards.get(pos).time.isEmpty())
                ((TextView) v.findViewById(R.id.Distance)).setText(programsCards.get(pos).time.substring(0,programsCards.get(pos).time.length()-4)+":"+programsCards.get(pos).time.substring(programsCards.get(pos).time.length()-4,programsCards.get(pos).time.length()));
            else
                ((TextView) v.findViewById(R.id.Distance)).setText("All Day");
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((MainActivity) getActivity()).setIndividualProgram(programsCards.get(pos).SID, false);
                    ((MainActivity) getActivity()).individualProgram._SID = programsCards.get(pos).SID;
                    ((MainActivity) getActivity()).individualProgram._description = " ";
                    ((MainActivity) getActivity()).individualProgram._programType = " ";
                    ((MainActivity) getActivity()).individualProgram._department = " ";
                    ((MainActivity) getActivity()).individualProgram._time = " ";
                    ((MainActivity) getActivity()).individualProgram._location = " ";
                    ((MainActivity) getActivity()).individualProgram._programTitle = " ";
                    ((MainActivity) getActivity()).individualProgram._programTitle = programsCards.get(pos).getName();
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
                        ((MainActivity) getActivity()).individualProgram._location = "Not Available";
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
                    if(((MainActivity) getActivity()).programInfos.programType.isEmpty()) {
                        ((MainActivity) getActivity()).individualProgram._programType = "Not Available";
                    }
                    else {
                        ((MainActivity) getActivity()).individualProgram._programType = ((MainActivity) getActivity()).programInfos.programType;
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
                        ((MainActivity) getActivity()).individualProgram._time = "Not Available";
                        //((MainActivity) getActivity()).individualProgram.time.setText("Not Available");
                    }

                    ((MainActivity) getActivity()).individualProgram._campus = ((MainActivity) getActivity()).programInfos.campus;
                    ((MainActivity) getActivity()).individualProgram._description = ((MainActivity) getActivity()).programInfos.programDescription;

                }
            });

            return v;
        }

    }
}