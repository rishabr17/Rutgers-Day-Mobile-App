package com.example.rishabravikumar.rutgersday;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by Rishab Ravikumar on 3/26/2018.
 */

    public class ProgramsCard {
        String name;
        String time;
        String SID;
        String campus;
        double miles;
        LatLng location;

        public ProgramsCard(String name, String time, String SID, double miles, LatLng location) {
            this.name = name;
            this.time = time;
            this.SID = SID;
            this.miles = miles;
            this.location = location;
        }
        public ProgramsCard(String campus, String name, String time, String SID,  double miles, LatLng location) {
            this.campus = campus;
            this.name = name;
            this.time = time;
            this.SID = SID;
            this.miles = miles;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getMiles(){
            return miles;
        }

        public void setMiles(){
            this.miles = miles;
        }

        public JSONObject getJSON(){
            JSONObject programsJSON = new JSONObject();
            try {
                programsJSON.put("campus", campus);
                programsJSON.put("name", name);
                programsJSON.put("time", time);
                programsJSON.put("SID", SID);
                programsJSON.put("latitude", Double.toString(location.latitude));
                programsJSON.put("longitude", Double.toString(location.longitude));
            }catch(Exception e){}
            return programsJSON;
        }

    }
