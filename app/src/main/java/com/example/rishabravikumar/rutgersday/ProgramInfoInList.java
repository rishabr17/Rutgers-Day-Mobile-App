package com.example.rishabravikumar.rutgersday;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rishab Ravikumar on 3/13/2018.
 */

///////////// OBJECT THAT STORES PROPERTIES OF PROGRAMS ///////////////
public class ProgramInfoInList {
    String SID;
    String title;
    String duration;
    LatLng latLng;
    String campus;

    public ProgramInfoInList(String SID, String title, String duration, LatLng latLng){
        this.SID = SID;
        this.title = title;
        this.duration = duration;
        this.latLng = latLng;
    }
    public ProgramInfoInList(String campus, String SID, String title, String duration, LatLng latLng){
        this.campus = campus;
        this.SID = SID;
        this.title = title;
        this.duration = duration;
        this.latLng = latLng;
    }
}
