package com.example.cw2runningtracker;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    //Declare required variables
    private TrackerRepository trackerRepository;
    private final LiveData<List<RunningTracker>> allRuns;
    public Date startTime;
    public Date endTime;
    public List<Location> locations;
    public LiveData<RunningTracker> run;

    public String coords;

    //Constructor to initialise repository and LiveData List of all runs
    public MainViewModel(@NonNull Application application) {
        super(application);
        trackerRepository = new TrackerRepository(application);
        allRuns = trackerRepository.getAllRuns();
    }

    //Method to get all runs
    LiveData<List<RunningTracker>> getAllRuns(){ return allRuns; }

    //Methods to set Start and End times
    public void setStartTime(Date date){ startTime = date; }
    public void setEndTime(Date date){ endTime = date; }
}
