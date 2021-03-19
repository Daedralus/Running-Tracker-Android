package com.example.cw2runningtracker;

import android.app.Application;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AddRunViewModel extends AndroidViewModel {

    TrackerRepository repository;
    RunningTrackerDao runningTrackerDao;

    //Bound data that the UI uses
    public String start;
    public String end;
    public String time;
    public String distance;
    public String speed;
    public String coords;

    //Format to avoid too many decimal points
    DecimalFormat numberFormat = new DecimalFormat("#.####");

    //Constructor to initialise repository
    public AddRunViewModel(@NonNull Application application) {
        super(application);
        repository = new TrackerRepository(application);
    }

    //Method to return formatted time (HH:MM:SS) from total seconds
    public String calculateTime(int time){
        Date d = new Date(time * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String output = df.format(d);
        return output;
    }


    //Method to calculate distance using list of coordinates
    public String calculateDistance(String coords){
        double distance = 0;
        //Initialise location, another for next location
        Location location = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);
        String[] tuples = coords.split(":");

        //For every location, get distance to next location and add to distance
        for(int i = 0; i < tuples.length - 1; i++){
            String[] loc1 = tuples[i].split(",");
            String[] loc2 = tuples[i+1].split(",");
            location.setLatitude(Float.parseFloat(loc1[0]));
            location2.setLatitude(Float.parseFloat(loc2[0]));
            location.setLongitude(Float.parseFloat(loc1[1]));
            location2.setLongitude(Float.parseFloat(loc2[1]));

            distance += location.distanceTo(location2);
        }
        //Convert to string format to display in UI
        String output = numberFormat.format(distance);
        output += "m";
        return output;
    }

    //Calculate speed from basic DST formula
    public String calculateSpeed(int time){
        String dis = distance.substring(0, distance.length()-2); //remove unit for conversion
        double d = Double.parseDouble(dis);
        double ms = d/time;
        //distance in meters and time in seconds give m/s, we want kmh
        double kmh = ms * 3.6;

        //Revert back to string for UI
        String output = numberFormat.format(kmh);
        output += "kmh";
        return output;
    }

    //Insert run to database
    public void insert(RunningTracker runningTracker){
        repository.insert(runningTracker);
    }
}
