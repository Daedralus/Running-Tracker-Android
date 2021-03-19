package com.example.cw2runningtracker;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

//TrackerRepository built on top of MartinRoomMVVM "MyRepository" Demo
public class TrackerRepository {
    //The purposes of all following variables and methods are clear from their names and do not
    //need individual comments

    private RunningTrackerDao runningTrackerDao;

    private LiveData<List<RunningTracker>> allRuns;

    TrackerRepository(Application application){
        TrackerRoomDatabase db = TrackerRoomDatabase.getDatabase(application);
        runningTrackerDao = db.runningTrackerDao();
        allRuns = runningTrackerDao.getAll();
    }

    LiveData<RunningTracker> getRunById(int id){ return runningTrackerDao.getRunById(id); }

    LiveData<List<RunningTracker>> getAllRuns(){ return allRuns; }
    public void insert(RunningTracker runningTracker){
        TrackerRoomDatabase.databaseWriteExecutor.execute(() ->{
            runningTrackerDao.insert(runningTracker);
        });
    }

    public void deleteRun(int id){
        TrackerRoomDatabase.databaseWriteExecutor.execute(() ->{
            runningTrackerDao.deleteRun(id);
        });
    }
}
