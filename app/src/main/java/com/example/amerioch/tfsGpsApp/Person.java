package com.example.amerioch.tfsGpsApp;

import android.location.GpsSatellite;
import android.location.Location;

/**
 * Created by ramon on 26/07/15.
 */
public class Person {
    public String name;
    public String distance;

    public Person(String name, Location loc){
        this.name = name;
        this.distance = calculateGPSLocation(loc);
    }

    private String calculateGPSLocation(Location gpsLocation){
        String dist="";
        //Calculate GPS location
        //Return Ex: 50m, 60km
        return dist;
    }
}
