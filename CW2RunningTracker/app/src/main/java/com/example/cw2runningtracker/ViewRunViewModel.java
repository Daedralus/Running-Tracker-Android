package com.example.cw2runningtracker;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewRunViewModel extends AndroidViewModel {

    private TrackerRepository repository;

    public LiveData<RunningTracker> run;

    public String coords;
    public int id;

    public List<LatLng> map = new ArrayList<>();

    //Instantiate repository in constructor
    public ViewRunViewModel(@NonNull Application application) {
        super(application);
        repository = new TrackerRepository(application);
    }

    public void getRunById(int id){ run = repository.getRunById(id); }
    public LiveData<RunningTracker> getRun(){ return run; }

    public void convertCoords(String coords){
        String[] allCoords = coords.split(":"); //Split to individual latlng
        for(int i = 0; i < allCoords.length; i++){
            String[] latLng = allCoords[i].split(","); //Split lat and lng
            double lat = Double.parseDouble(latLng[0]);
            double lng = Double.parseDouble(latLng[1]);
            LatLng point = new LatLng(lat,lng);
            map.add(point);
        }
    }

    public List<LatLng> getMap(){ return map; }
    public void deleteRun(){ repository.deleteRun(id); }

}
