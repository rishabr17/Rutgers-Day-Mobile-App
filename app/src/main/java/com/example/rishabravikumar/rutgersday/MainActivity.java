package com.example.rishabravikumar.rutgersday;

import android.Manifest;
import android.app.Notification;
import android.graphics.drawable.Icon;
import android.location.LocationListener;
import android.content.Context;
import android.app.ProgressDialog;
import android.app.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;
import java.util.UUID;
import android.app.NotificationManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.content.pm.PackageManager;
import android.app.PendingIntent;
import java.util.Calendar;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.AlarmManager;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.NotificationCompatBase;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.example.rishabravikumar.rutgersday.SingleShotLocationProvider.GPSCoordinates;
import com.example.rishabravikumar.rutgersday.ProgramInfoInList;
import com.viethoa.models.AlphabetItem;
import android.app.ActivityManager.RunningServiceInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.view.Menu;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.os.SystemClock;
import java.util.Arrays;
import android.support.design.widget.FloatingActionButton;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import android.widget.RelativeLayout;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    boolean interestss = false;
    boolean isProgramSelected = false;
    boolean isItemSelected = false;
    boolean isFoodSelected = false;
    BottomNavigationView bottomNavigationView;
    MapFragment mapFragment;
    boolean newBrunswick = false;
    boolean newark = false;
    boolean camden = false;
    boolean about = false;
    boolean foodtrue = false;
    CampusSelectorFragment campusSelectorFragment;
    FloatingActionButton myFab;
    ArrayList<ProgramsCard> itineraryCards = new ArrayList<ProgramsCard>();
    GeneralInfoFragment generalInfoFragment;
    RelativeLayout relativeLayout;
    InfoFragment infoFragment;
    GeoListFragment foodsFragment;
    SocialMediaFragment socialMediaFragment;
    BusLoopFragment busLoopFragment;
    TutorialFragment tutorialFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Thread timer= new Thread()
        {
            public void run()
            {
                try
                {
                    //Display for 3 seconds
                    sleep(3000);
                }
                catch (InterruptedException e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        };
        timer.start();
        ///////////// DONT TOUCH THIS STUFF, HAS TO DO WITH THE TOP BAR ///////////////
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255,154,203,231)));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.temp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myFab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadItinerary();
            }
        });
        myFab.setVisibility(View.GONE);

        ///////////// STORES PARKING LOTS/BUS STOPS INFORMATION ///////////////
        try{
            getLocation(this);
        }catch(Exception e){}
        try{
            ArrayList<ParkingLotInfo>[] arr = getLots();
            lotsInfo = arr[0];
            stopsInfo = arr[1];
        }catch(Exception e){}
        try{
            searchFragment.getPrograms(latitudes, longitudes);
        }catch(Exception e){}

        String temp = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("Itinerary", "notfound");
        if(!temp.equals("notfound")){
            itineraryCards = new ArrayList<>();
            JSONArray itinjson;
            itinjson = null;
            try {
                itinjson = new JSONArray(temp);
            }catch(Exception e){}
            for(int i = 0; i < itinjson.length(); i++) {
                Log.d("itinerari",itinjson.toString());
                JSONObject jsonObject = null;
                try {
                    jsonObject = itinjson.getJSONObject(i);
                    Log.d("itinerari",jsonObject.toString());
                    itineraryCards.add(new ProgramsCard(jsonObject.getString("campus"), jsonObject.getString("name"), jsonObject.getString("time"), jsonObject.getString("SID"), (double)0, new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")))));
                }catch(Exception e){
                }
            }
        }


        relativeLayout = (RelativeLayout)findViewById(R.id.loadingPanel);

        ///////////// INITIALIZE MAIN SCREENS ///////////////
        tutorialFragment = new TutorialFragment();
        campusSelectorFragment = new CampusSelectorFragment();
        infoFragment = new InfoFragment();
        socialMediaFragment = new SocialMediaFragment();
        busLoopFragment = new BusLoopFragment();
        mapFragment = new MapFragment();
        foodsFragment = new GeoListFragment();
        final AboutFragment aboutFragment = new AboutFragment();
        generalInfoFragment = new GeneralInfoFragment();
        final ProgramSelectorFragment programSelectorFragment = new ProgramSelectorFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        boolean saved = false;
        if(empty || PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("Selected","empty").equals("empty")) {
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
            fragmentTransaction1.add(R.id.content_frame, campusSelectorFragment, "campusSelector");
            fragmentTransaction1.show(campusSelectorFragment);
            Log.d("hey","show");
            fragmentTransaction1.commit();
            empty = false;
        }else{
            saved = true;
            switch(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("Selected","")){
                case("New Brunswick"):
                    Log.d("works","hey");
                    newBrunswick = true;
                    break;
                case("Newark"):
                    newark = true;
                    setProgramsList("l2");
                    break;
                case("Camden"):
                    camden = true;
                    setProgramsList("l7");
                    break;
            }
        }
        if(saved) {
            Log.d("hey", "hey");
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
            fragmentTransaction1.add(R.id.content_frame, mapFragment);
            //generalInfoFragment.getGeneralInfo();
            fragmentTransaction1.show(mapFragment);
            fragmentTransaction1.commit();
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        fragmentTransaction.add(R.id.content_frame, aboutFragment, "about");
        fragmentTransaction.hide(aboutFragment);
        fragmentTransaction.add(R.id.content_frame, generalInfoFragment, "generalinfo");
        fragmentTransaction.hide(generalInfoFragment);
        fragmentTransaction.add(R.id.content_frame, programSelectorFragment, "selector");
        fragmentTransaction.hide(programSelectorFragment);
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.commit();


        ///////////// SWITCHES BETWEEN SECTIONS OF APP (MAPS, PROGRAMS, INFO, AND ABOUT) ///////////////

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavigationView.getMenu().findItem(R.id.aboutIcon).setChecked(false);
                bottomNavigationView.getMenu().findItem(R.id.infoIcon).setChecked(false);
                bottomNavigationView.getMenu().findItem(R.id.programIcon).setChecked(false);
                bottomNavigationView.getMenu().findItem(R.id.mapsIcon).setChecked(false);
                item.setChecked(true);
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch(item.getTitle().toString()){
                    case("Maps"):
                        program = false;
                        info = false;
                        about = false;
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.hide(generalInfoFragment);
                        fragmentTransaction.hide(aboutFragment);
                        if(fragmentManager.findFragmentByTag("search") != null)
                            fragmentTransaction.hide(searchFragment);
                        if(fragmentManager.findFragmentByTag("individual") != null)
                            fragmentTransaction.hide(individualProgram);
                        if(fragmentManager.findFragmentByTag("programsList") != null)
                            fragmentTransaction.hide(programsListFragment);
                        if(fragmentManager.findFragmentByTag("browse") != null)
                            fragmentTransaction.hide(programsFragment);
                        fragmentTransaction.hide(programSelectorFragment);
                        fragmentTransaction.hide(stopListFragment);
                        fragmentTransaction.hide(foodsFragment);
                        fragmentTransaction.hide(parkingListFragment);
                        fragmentTransaction.hide(generalInfoFragment);
                        fragmentTransaction.hide(infoFragment);
                        fragmentTransaction.hide(findividualProgram);
                        fragmentTransaction.hide(socialMediaFragment);
                        fragmentTransaction.hide(busLoopFragment);
                        fragmentTransaction.show(mapFragment);
                        fragmentTransaction.commit();
                        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        return false;
                    case("Programs"):
                        program = true;
                        info = false;
                        about = false;
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.hide(aboutFragment);
                        fragmentTransaction.hide(mapFragment);
                        fragmentTransaction.hide(mapFragment);
                        fragmentTransaction.hide(stopListFragment);
                        fragmentTransaction.hide(foodsFragment);
                        fragmentTransaction.hide(parkingListFragment);
                        fragmentTransaction.hide(socialMediaFragment);
                        fragmentTransaction.hide(busLoopFragment);
                        fragmentTransaction.hide(findividualProgram);
                        fragmentTransaction.hide(infoFragment);
                        fragmentTransaction.hide(generalInfoFragment);
                        if(fragmentManager.findFragmentByTag("search") != null)
                            fragmentTransaction.show(searchFragment);
                        else if(fragmentManager.findFragmentByTag("individual") != null){
                            fragmentTransaction.show(individualProgram);
                        }
                        else if(fragmentManager.findFragmentByTag("programsList") != null) {
                            fragmentTransaction.show(programsListFragment);
                        }
                        else if(fragmentManager.findFragmentByTag("browse") != null){
                            fragmentTransaction.show(programsFragment);
                        }
                        else if(!newark && !camden)
                            fragmentTransaction.show(programSelectorFragment);
                        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.commit();
                        return false;
                    case("Info"):
                        generalInfoFragment.getGeneralInfo();
                        program = false;
                        info = true;
                        about = false;
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.hide(aboutFragment);
                        fragmentTransaction.hide(socialMediaFragment);
                        fragmentTransaction.hide(mapFragment);
                        if(fragmentManager.findFragmentByTag("search") != null)
                            fragmentTransaction.hide(searchFragment);
                        if(fragmentManager.findFragmentByTag("individual") != null)
                            fragmentTransaction.hide(individualProgram);
                        if(fragmentManager.findFragmentByTag("programsList") != null)
                            fragmentTransaction.hide(programsListFragment);
                        if(fragmentManager.findFragmentByTag("browse") != null)
                            fragmentTransaction.hide(programsFragment);
                        fragmentTransaction.hide(programSelectorFragment);


                        if(fragmentManager.findFragmentByTag("busloop")!= null && fragmentManager.findFragmentByTag("busloop").isAdded()){
                            fragmentTransaction.show(busLoopFragment);
                        }
                        else if(fragmentManager.findFragmentByTag("findividual")!= null && fragmentManager.findFragmentByTag("findividual").isAdded()){
                            fragmentTransaction.show(findividualProgram);
                        }
                        else if(fragmentManager.findFragmentByTag("food") != null && fragmentManager.findFragmentByTag("food").isAdded())
                            fragmentTransaction.show(foodsFragment);
                        else if(fragmentManager.findFragmentByTag("emergency") != null && fragmentManager.findFragmentByTag("emergency").isAdded())
                            fragmentTransaction.show(infoFragment);
                        else if(fragmentManager.findFragmentByTag("parkingList") != null && fragmentManager.findFragmentByTag("parkingList").isAdded())
                            fragmentTransaction.show(parkingListFragment);
                        else if(fragmentManager.findFragmentByTag("stopList") != null && fragmentManager.findFragmentByTag("stopList").isAdded())
                            fragmentTransaction.show(stopListFragment);
                        else
                            fragmentTransaction.show(generalInfoFragment);
                        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.commit();
                        return false;
                    case("About"):
                        program = false;
                        info = false;
                        about = true;
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.hide(busLoopFragment);
                        fragmentTransaction.hide(mapFragment);
                        if(fragmentManager.findFragmentByTag("search") != null)
                            fragmentTransaction.hide(searchFragment);
                        if(fragmentManager.findFragmentByTag("individual") != null)
                            fragmentTransaction.hide(individualProgram);
                        if(fragmentManager.findFragmentByTag("programsList") != null)
                            fragmentTransaction.hide(programsListFragment);
                        if(fragmentManager.findFragmentByTag("browse") != null)
                            fragmentTransaction.hide(programsFragment);
                        fragmentTransaction.hide(programSelectorFragment);
                        fragmentTransaction.hide(stopListFragment);
                        fragmentTransaction.hide(findividualProgram);
                        fragmentTransaction.hide(parkingListFragment);
                        fragmentTransaction.hide(foodsFragment);
                        fragmentTransaction.hide(infoFragment);
                        fragmentTransaction.hide(generalInfoFragment);
                        if(fragmentManager.findFragmentByTag("social") != null && fragmentManager.findFragmentByTag("social").isAdded()){
                            fragmentTransaction.show(socialMediaFragment);
                        }else {
                            fragmentTransaction.show(aboutFragment);
                        }
                        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.commit();
                        return false;
                }
                return false;
            }
        });

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(this);
            sendLocation(latitudes, longitudes);
        }
        if(!PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("Tutorial","empty").equals("yes")){
            Log.d("hey","ey");
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
            fragmentTransaction1.hide(campusSelectorFragment);
            fragmentTransaction1.add(R.id.content_frame, tutorialFragment);
            fragmentTransaction1.show(tutorialFragment);
            fragmentTransaction1.commit();
            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("Tutorial","yes").apply();
        }

        Calendar alarm = Calendar.getInstance();
        alarm.set(2018,3,28,13,00,00);
        Calendar alarm2 = Calendar.getInstance();
        alarm2.set(2018,3,28,15,00,00);
        scheduleNotification(makeNotification(alarm.getTimeInMillis(), 1), alarm.getTimeInMillis(), 1);
        scheduleNotification(makeNotification(alarm2.getTimeInMillis(), 2), alarm2.getTimeInMillis(), 2);

    }
    public Notification makeNotification(long delay, int requestcode){
        /*Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
*/
        /*Notification.Builder b = new Notification.Builder(this);
        Calendar alarm = Calendar.getInstance();
        alarm.set(2018,3,12,4,37);
        b.setAutoCancel(true)
                .setDefaults(Notification.PRIORITY_HIGH | Notification.FLAG_ONLY_ALERT_ONCE)
                .setWhen(alarm.getTimeInMillis())
                .setShowWhen(true)
                .setOnlyAlertOnce(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.appicon))
                .setSmallIcon(R.drawable.appicon)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle("Tell us how you liked Rutgers Day.")
                .setContentText("Take the survey and be eligible for a gift card!")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentInfo("Info");*/
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rutgers.ca1.qualtrics.com/jfe/form/SV_byl6QWjT9pq4gpT"));

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), requestcode, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent);
        builder.setWhen(delay);
        builder.setShowWhen(true);
        builder.setContentTitle("Tell us how you liked Rutgers Day.");
        builder.setContentText("Take the survey and be eligible for a gift card!");
        builder.setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND);
        builder.setSmallIcon(R.drawable.notificationicon);
        //builder.setColor(Color.argb(255,33,117,165));

        return builder.build();
        //return b.build();
    }
    private void scheduleNotification(Notification notification, long delay, int requestcode) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra("string1", requestcode);
        notificationIntent.putExtra("string2", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestcode, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        //long futureInMillis = delay - System.currentTimeMillis();
        long futureInMillis = delay;
        Log.d("notification", Long.toString(System.currentTimeMillis()));
        Log.d("notification", Long.toString(futureInMillis));
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }
    private void scheduleNotification2(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher2.class);
        notificationIntent.putExtra("string1", 0);
        notificationIntent.putExtra("string2", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        //long futureInMillis = delay - System.currentTimeMillis();
        long futureInMillis = delay;
        Log.d("notification", Long.toString(futureInMillis));
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(this);
            sendLocation(latitudes, longitudes);
        }
    }

    public void sendLocation(final double lati, final double longi){

        Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String url = "https://api.rutgersday.rutgers.edu/analytics?long="+Double.toString(longi)+"&lat="+Double.toString(lati)+"&UID="+"ANDROID-"+createTransactionID();
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Authorized-Token", "9a5de304-73fd-4a1c-a326-ca710d777cef");

                    InputStream programStream = con.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(programStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    StringBuilder sb = new StringBuilder();
                    while (line != null) {
                        sb.append(line);
                        line = bufferedReader.readLine();
                    }

                }catch(Exception e){}
            }
        });
        internetThread.start();
    }

    public String createTransactionID() throws Exception{
        if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("UUID","empty").equals("empty"))
            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("UUID", UUID.randomUUID().toString().replaceAll("-", "").toUpperCase()).apply();
        return PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("UUID","empty");
    }

    public void loadMapsFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager2.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, mapFragment);
        fragmentTransaction.hide(mapFragment);
        fragmentTransaction.commit();
    }
    public void loadInfoFragment(boolean isEmergency, boolean isRestrooms){
        relativeLayout.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        if(fragmentManager3.findFragmentByTag("emergency") == null || !fragmentManager3.findFragmentByTag("emergency").isAdded()) {
            fragmentTransaction3.add(R.id.content_frame, infoFragment, "emergency");
            fragmentTransaction3.hide(infoFragment);
        }else{
            fragmentTransaction3.remove(infoFragment);
            fragmentTransaction3.add(R.id.content_frame, infoFragment, "emergency");
            fragmentTransaction3.hide(infoFragment);
        }
        if(isEmergency){
            infoFragment.emergency = true;
            infoFragment.restrooms = false;
        }else if(!isEmergency && !isRestrooms){
            infoFragment.emergency = false;
            infoFragment.restrooms = false;
        }else{
            infoFragment.emergency = false;
            infoFragment.restrooms = true;
        }
        fragmentTransaction3.show(infoFragment);
        fragmentTransaction3.hide(fragmentManager3.findFragmentByTag("generalinfo"));
        fragmentTransaction3.commit();
    }

    public void loadSocialFragment(){
        relativeLayout.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        if(fragmentManager3.findFragmentByTag("social") == null || !fragmentManager3.findFragmentByTag("social").isAdded()) {
            fragmentTransaction3.add(R.id.content_frame, socialMediaFragment, "social");
            fragmentTransaction3.hide(socialMediaFragment);
        }else{
            fragmentTransaction3.remove(socialMediaFragment);
            fragmentTransaction3.add(R.id.content_frame, socialMediaFragment, "social");
            fragmentTransaction3.hide(socialMediaFragment);
        }

        fragmentTransaction3.show(socialMediaFragment);
        fragmentTransaction3.hide(fragmentManager3.findFragmentByTag("about"));
        fragmentTransaction3.commit();
    }
    public void loadBusFragment(){
        relativeLayout.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        if(fragmentManager3.findFragmentByTag("busloop") == null || !fragmentManager3.findFragmentByTag("busloop").isAdded()) {
            fragmentTransaction3.add(R.id.content_frame, busLoopFragment, "busloop");
            fragmentTransaction3.hide(busLoopFragment);
        }else{
            fragmentTransaction3.remove(busLoopFragment);
            fragmentTransaction3.add(R.id.content_frame, busLoopFragment, "busloop");
            fragmentTransaction3.hide(busLoopFragment);
        }

        fragmentTransaction3.show(busLoopFragment);
        fragmentTransaction3.hide(fragmentManager3.findFragmentByTag("generalinfo"));
        fragmentTransaction3.commit();
    }

    public void loadFoodsFragment(int code){
        foodtrue = true;
        FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

        if(code == 1) {
            foodsFragment.getFoodsPrograms(0, 0, "hey", null);
        }
        else if(code == 2){
            foodsFragment.getPrograms(0, 0, "l2p76", null);
        }else if(code == 3){
            foodsFragment.getPrograms(0, 0, "l7p76", null);
        }
        foodsFragment.foodtrue = true;

        if(fragmentManager3.findFragmentByTag("food") == null || !fragmentManager3.findFragmentByTag("food").isAdded()) {
            fragmentTransaction3.add(R.id.content_frame, foodsFragment, "food");
            fragmentTransaction3.hide(foodsFragment);
        }
        fragmentTransaction3.show(foodsFragment);
        fragmentTransaction3.hide(fragmentManager3.findFragmentByTag("generalinfo"));
        fragmentTransaction3.commit();
    }

    ///////////// GETS CURRENT LOCATION ///////////////
    ArrayList<ParkingLotInfo> lotsInfo;
    ArrayList<ParkingLotInfo> stopsInfo;
    double latitudes;
    double longitudes;
    FragmentManager fragmentManager3 = getSupportFragmentManager(); // MANAGES GENERAL INFO FRAGMENTS
    GeoListFragment parkingListFragment = new GeoListFragment(); // PARKING LIST FRAGMENT
    public void getLocation(Context context) {
        SingleShotLocationProvider.requestSingleUpdate(context, new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(GPSCoordinates location) {
                        latitudes = location.latitude;
                        longitudes = location.longitude;
                    }
                });
    }
    ///////////// SWITCHES TO PARKING LIST SCREEN ///////////////
    public void loadParkingFragment(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(this);
            parkingListFragment.lots = lotsInfo;
            parkingListFragment.getLots(latitudes, longitudes, newBrunswick, newark, camden);
        }
        else {
            parkingListFragment.lots = lotsInfo;
            parkingListFragment.getLots(0, 0, newBrunswick, newark, camden);
        }
        FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

        if(fragmentManager3.findFragmentByTag("parkingList") == null || !fragmentManager3.findFragmentByTag("parkingList").isAdded()) {
            fragmentTransaction3.add(R.id.content_frame, parkingListFragment, "parkingList");
            fragmentTransaction3.hide(parkingListFragment);
        }
        fragmentTransaction3.show(parkingListFragment);
        fragmentTransaction3.hide(fragmentManager3.findFragmentByTag("generalinfo"));
        fragmentTransaction3.commit();
        Toast.makeText(this, "Click To Launch Google Maps",
                Toast.LENGTH_SHORT).show();
    }
    GeoListFragment stopListFragment = new GeoListFragment(); // PARKING LIST FRAGMENT
    ///////////// SWITCHES TO PARKING LIST SCREEN ///////////////
    public void loadStopsFragment(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(this);
            stopListFragment.stops = stopsInfo;
            stopListFragment.getStops(latitudes, longitudes);
        }
        else {
            stopListFragment.stops = stopsInfo;
            stopListFragment.getStops(0, 0);
        }
        FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
        fragmentTransaction3.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        if(fragmentManager3.findFragmentByTag("stopList") == null || !fragmentManager3.findFragmentByTag("stopList").isAdded()) {
            fragmentTransaction3.add(R.id.content_frame, stopListFragment, "stopList");
            fragmentTransaction3.hide(stopListFragment);
        }
        fragmentTransaction3.show(stopListFragment);
        fragmentTransaction3.hide(fragmentManager3.findFragmentByTag("generalinfo"));
        fragmentTransaction3.commit();
        Toast.makeText(this, "Click To Launch Google Maps",
                Toast.LENGTH_SHORT).show();
    }

    FragmentManager fragmentManager2 = getSupportFragmentManager(); // MANAGES PROGRAMS FRAGMENTS
    ProgramsFragment programsFragment = new ProgramsFragment(); // BROWSING PROGRAMS FRAGMENT
    GeoListFragment programsListFragment = new GeoListFragment(); // PROGRAMS LIST FRAGMENT
    ProgramIndividualFragment individualProgram = new ProgramIndividualFragment(); // PROGRAM INFO
    ProgramIndividualFragment findividualProgram = new ProgramIndividualFragment();
    ItineraryIndividualFragment itineraryIndividualFragment = new ItineraryIndividualFragment();
    ///////////// SWITCHES TO PROGRAM BROWSING SCREEN ///////////////
    public void programBrowser(){
        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
        fragmentTransaction2.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        if(fragmentManager2.findFragmentByTag("browse") == null) {
            fragmentTransaction2.add(R.id.content_frame, programsFragment, "browse");
        }
        fragmentTransaction2.hide(fragmentManager2.findFragmentByTag("selector"));
        fragmentTransaction2.show(programsFragment);
        fragmentTransaction2.addToBackStack(null);
        fragmentTransaction2.commit();
    }


    //////////////////// DISPLAYS ITINERARY ///////////////////////////
    ItineraryFragment itineraryFragment = new ItineraryFragment();

    public void loadItinerary(){
        itineraryFragment.getItinerary(itineraryCards);
        itineraryFragment.show(fragmentManager2, "itinerary");
    }

    ///////////// SWITCHES TO PROGRAM INFORMATION SCREEN ///////////////
    public void setProgramsList(String code){
        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
        fragmentTransaction2.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        programsListFragment = new GeoListFragment();
        programsListFragment.foodtrue = false;
        fragmentTransaction2.add(R.id.content_frame, programsListFragment, "programsList");
        if(newark || camden || code.equalsIgnoreCase("l8") || code.equalsIgnoreCase("l6") || code.equalsIgnoreCase("l5") || code.equalsIgnoreCase("bigR") || code.equalsIgnoreCase("l8t51")|| code.equalsIgnoreCase("l5t51")|| code.equalsIgnoreCase("scarletstage")) {
            programsListFragment.getPrograms(latitudes, longitudes, code, null);
            interestss = false;
        }else {
            programsListFragment.getInterestPrograms(latitudes, longitudes, code, null);
            interestss = true;
        }

        if(!newark && !camden)
            fragmentTransaction2.hide(fragmentManager2.findFragmentByTag("browse"));

        fragmentTransaction2.show(programsListFragment);

        if(!newark && !camden)
            fragmentTransaction2.addToBackStack(null);
        else
            fragmentTransaction2.hide(programsListFragment);

        fragmentTransaction2.commit();
    }

    ProgramInfo programInfos;
    ///////////// SWITCHES TO PROGRAM INFORMATION SCREEN ///////////////
    public void setIndividualProgram(final String listCode, boolean isItinerary){
        final Thread internetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL programURL = null;
                HttpURLConnection programConnection = null;
                InputStream programStream = null;
                InputStreamReader inputStreamReader = null;
                BufferedReader bufferedReader = null;
                try {
                    programURL = new URL("https://api.rutgersday.rutgers.edu/item/" + listCode);
                    programConnection = (HttpURLConnection) programURL.openConnection();
                    programConnection.setConnectTimeout(1500);
                    programConnection.setRequestProperty("X-Authorized-Token","9a5de304-73fd-4a1c-a326-ca710d777cef");

                    try {

                        programStream = programConnection.getInputStream();
                        inputStreamReader = new InputStreamReader(programStream, "UTF-8");
                        bufferedReader = new BufferedReader(inputStreamReader);
                        String line;
                        line = bufferedReader.readLine();
                        Log.d("line",line);
                        StringBuilder sb = new StringBuilder();
                        while (line != null) {
                            sb.append(line);
                            line = bufferedReader.readLine();
                        }
                        JSONObject jsonArray = new JSONObject(sb.toString());

                        programInfos = null;
                        LatLng lats;
                        try {
                            lats = new LatLng(Double.parseDouble(jsonArray.getString("latitude")), Double.parseDouble(jsonArray.getString("longitude")));
                        }catch(Exception e){
                            lats = null;
                        }
                        programInfos = new ProgramInfo(jsonArray.getString("program_title"), jsonArray.getString("program_description"), jsonArray.getString("campus"), jsonArray.getString("location"), lats, jsonArray.getString("name_of_department_unit_center_student_organization___to_be_listed_in_promotional_materials"), jsonArray.getString("select_which_best_describes_your_program"), jsonArray.getString("program_provider"), jsonArray.getString("program_timing"));
                        if(newark){
                            programInfos.latLng = new LatLng(40.742092,-74.174915);
                            programInfos.location = "Dana Library";
                        }
                        if(camden){
                            programInfos.latLng = new LatLng(39.948664,-75.122630);
                            programInfos.location = "Campus Center";
                        }
                    }catch(Exception e){
                        programInfos = new ProgramInfo("Not Available","Not Available","Not Available","Not Available",null,"Not Available","Not Available","Not Available","");
                        Thread.currentThread().interrupt();
                    }

                }catch(Exception e){}
            }
        });
        internetThread.start();
        try {
            internetThread.join();
        }catch(Exception e){}

        if(!isItinerary) {
            if(fragmentManager2.findFragmentByTag("food")== null || fragmentManager2.findFragmentByTag("food").isHidden() || !fragmentManager2.findFragmentByTag("food").isAdded()) {
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                fragmentTransaction2.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                if (fragmentManager2.findFragmentByTag("individual") == null) {
                    fragmentTransaction2.add(R.id.content_frame, individualProgram, "individual");
                }
                if (fragmentManager2.findFragmentByTag("search") != null && !fragmentManager2.findFragmentByTag("search").isHidden()) {

                    fragmentTransaction2.hide(fragmentManager2.findFragmentByTag("search"));
                }
                if (fragmentManager2.findFragmentByTag("programsList") != null && !fragmentManager2.findFragmentByTag("programsList").isHidden()) {

                    fragmentTransaction2.hide(fragmentManager2.findFragmentByTag("programsList"));
                }
                fragmentTransaction2.show(individualProgram);
                fragmentTransaction2.addToBackStack(null);
                fragmentTransaction2.commit();
            }else{
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                fragmentTransaction2.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                if (fragmentManager2.findFragmentByTag("findividual") == null) {
                    fragmentTransaction2.add(R.id.content_frame, findividualProgram, "findividual");
                }
                if (fragmentManager2.findFragmentByTag("food") != null && !fragmentManager2.findFragmentByTag("food").isHidden()) {

                    fragmentTransaction2.hide(fragmentManager2.findFragmentByTag("food"));
                }
                fragmentTransaction2.show(findividualProgram);
                fragmentTransaction2.commit();
            }
        }else{
            itineraryIndividualFragment.show(fragmentManager2, "itineraryindividual");
        }
    }

    SearchFragment searchFragment = new SearchFragment();
    public void searchPrograms(){
        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayout.bringToFront();
        getLocation(this);
        try{
            searchFragment.getPrograms(latitudes, longitudes);
        }catch(Exception e){}

        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
        fragmentTransaction2.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        if(fragmentManager2.findFragmentByTag("search") == null) {
            fragmentTransaction2.add(R.id.content_frame, searchFragment, "search");
        }
        fragmentTransaction2.hide(fragmentManager2.findFragmentByTag("selector"));
        fragmentTransaction2.show(searchFragment);
        fragmentTransaction2.addToBackStack(null);
        fragmentTransaction2.commit();

        relativeLayout.setVisibility(View.GONE);

    }

    public void removeLoader(){
        relativeLayout.setVisibility(View.GONE);
    }

    ///////////// OVERRIDES THE BACK BUTTON, POPS BACK STACK FOR PROGRAMS, INFO, AND ABOUT SCREENS ///////////////
    boolean program; // CHECKS TO SEE IF CURRENTLY ON PROGRAMS SCREEN
    boolean info;  // CHECKS TO SEE IF CURRENTLY ON ABOUT SCREEN
    @Override
    public void onBackPressed() {
        int count2 = fragmentManager2.getBackStackEntryCount(); // REFERS TO PROGRAMS STACK
        if(count2 != 0 && program){
            isProgramSelected = false;
            isItemSelected = false;
            fragmentManager2.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentManager2.popBackStack();
        }
        if(info && fragmentManager3.findFragmentByTag("parkingList") != null){
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("parkingList"));
            transaction.show(fragmentManager3.findFragmentByTag("generalinfo"));
            transaction.commit();
        }
        if(info && fragmentManager3.findFragmentByTag("stopList") != null){
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("stopList"));
            transaction.show(fragmentManager3.findFragmentByTag("generalinfo"));
            transaction.commit();
        }
        if(info && fragmentManager3.findFragmentByTag("emergency") != null){
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("emergency"));
            transaction.show(fragmentManager3.findFragmentByTag("generalinfo"));
            transaction.commit();
        }
        if(info && fragmentManager3.findFragmentByTag("findividual") != null){
            isFoodSelected = false;
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("findividual"));
            transaction.show(fragmentManager3.findFragmentByTag("food"));
            transaction.commit();
        }else if(info && fragmentManager3.findFragmentByTag("food") != null){
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("food"));
            transaction.show(fragmentManager3.findFragmentByTag("generalinfo"));
            transaction.commit();
        }else if(info && fragmentManager3.findFragmentByTag("busloop") != null){
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("busloop"));
            transaction.show(fragmentManager3.findFragmentByTag("generalinfo"));
            transaction.commit();
        }

        if(about && fragmentManager3.findFragmentByTag("social") != null){
            FragmentTransaction transaction = fragmentManager3.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.remove(fragmentManager3.findFragmentByTag("social"));
            transaction.show(fragmentManager3.findFragmentByTag("about"));
            transaction.commit();
        }
    }

    ///////////// READS PARKING LOT INFO AND BUS STOP INFO FROM JSONOBJECT ///////////////
    public ArrayList<ParkingLotInfo>[] getLots() {
        JSONArray arr;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        ArrayList<ParkingLotInfo> busstops = new ArrayList<>();
        ArrayList<ParkingLotInfo> lotslist = new ArrayList<>();
        try {
            //// TO CHANGE LOT/BUS STOP INFO, JUST UPLOAD NEW "lots.json" FILE IN res/raw, MAKE SURE IT FOLLOWS SAME FORMAT ////
            inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.lots));
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            StringBuilder sb = new StringBuilder();
            while(line != null){
                sb.append(line);
                line = bufferedReader.readLine();
            }
            JSONObject json = new JSONObject(sb.toString());
            arr = json.getJSONArray("row");
            for(int i = 0; i < arr.length(); i++){
                if (arr.getJSONObject(i).getString("Title").contains("Bus")) {
                    busstops.add(new ParkingLotInfo(arr.getJSONObject(i).getString("Title"), arr.getJSONObject(i).getString("Description"), arr.getJSONObject(i).getString("Campus"), arr.getJSONObject(i).getString("Location"), new LatLng(Double.parseDouble(arr.getJSONObject(i).getString("Lat")), Double.parseDouble(arr.getJSONObject(i).getString("Long")))));

                } else {
                    lotslist.add(new ParkingLotInfo(arr.getJSONObject(i).getString("Title"), arr.getJSONObject(i).getString("Description"), arr.getJSONObject(i).getString("Campus"), arr.getJSONObject(i).getString("Location"), new LatLng(Double.parseDouble(arr.getJSONObject(i).getString("Lat")), Double.parseDouble(arr.getJSONObject(i).getString("Long")))));
                }
            }
        }catch(Exception e){}
        return new ArrayList[]{lotslist,busstops};
    }

    ///////////// OBJECT THAT STORES PROPERTIES OF PARKING LOTS & BUS STOPS ///////////////
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
    ///////////// OBJECT THAT STORES PROPERTIES OF PROGRAMS ///////////////
    public class ProgramInfo {
        String title;
        String programDescription;
        String campus;
        String location;
        LatLng latLng;
        String department;
        String programType;
        String category;
        String time;

        public ProgramInfo(String title, String programDescription, String campus, String location, LatLng latLng, String department, String programType, String category, String time){
            this.title = title;
            this.programDescription = programDescription;
            this.campus = campus;
            this.location = location;
            this.latLng = latLng;
            this.department = department;
            this.programType = programType;
            this.category = category;
            this.time = time;
        }
    }

    //// MAKE THE QUERY, FILL THE LIST, GO TO SEARCH.
    public class ProgramsCardSearch {
        String name;
        String time;
        String SID;

        ProgramsCardSearch(String name, String time, String SID) {
            this.name = name;
            this.time = time;
            this.SID = SID;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        JSONArray jsonArray = new JSONArray();
        for(ProgramsCard card : itineraryCards){
            jsonArray.put(card.getJSON());
        }
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("Itinerary", jsonArray.toString()).apply();
        Log.d("itinerary",PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("Itinerary","empti"));
    }
    boolean empty = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            loadItinerary();
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeEmpty(){
        JSONArray jsonArray = new JSONArray();
        for(ProgramsCard card : itineraryCards){
            jsonArray.put(card.getJSON());
        }
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("Itinerary", jsonArray.toString()).apply();
        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("Selected", "empty").apply();
        Log.d("heyy",PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("Selected","not"));
    }

}


