package com.example.cw2runningtracker;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.cw2runningtracker.databinding.ActivityAddRunBinding;
import com.example.cw2runningtracker.databinding.ActivityStatsBinding;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StatsActivity extends AppCompatActivity {

    //Declare db, dao, viewmodal and list
    private List<RunningTracker> runs = new ArrayList<>();
    private TrackerRoomDatabase db;
    private RunningTrackerDao runningTrackerDao;
    private StatsViewModel viewModel;

    private static DecimalFormat decimalFormat = new DecimalFormat("#.####");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityStatsBinding v = ActivityStatsBinding.inflate(LayoutInflater.from(this));
        setContentView(v.getRoot());

        //Initialise db, dao and viewmodel
        db = TrackerRoomDatabase.getDatabase(getApplicationContext());
        runningTrackerDao = db.runningTrackerDao();

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(StatsViewModel.class);
        v.setViewmodel(viewModel);
        v.setLifecycleOwner(this);

        //Set viewmodel to observe all runs and then perform required logic to calculate desired data
        viewModel.getAllRuns().observe(this, runList ->{

            //Times, distances and speeds lists are extracted from runs for individual calculations
            List<String> times = new ArrayList<>();
            List<String> distances = new ArrayList<>();
            List<String> speeds = new ArrayList<>();

            String km = "km";

            //Format time speed distance into values that can be manipulated easier
            for(int i = 0; i < runList.size(); i ++){
                String time = runList.get(i).getTime();
                String[] hms = time.split(":");

                //Convert HH:MM:SS back to seconds for easier calculations
                //1 minute = 60 seconds, 1 hour = 60 minutes = 3600 seconds
                int seconds = Integer.parseInt(hms[0]) * 3600 + Integer.parseInt(hms[1]) * 60 + Integer.parseInt(hms[2]);
                times.add(Integer.toString(seconds));

                //Drop meters or km from distance for type conversion
                String distance = runList.get(i).getDistance();
                if(distance.contains(km)){
                    distance = distance.substring(0, distance.length()-3); //drop km
                }else{
                    distance = distance.substring(0, distance.length()-2); //drop m
                }
                distances.add(distance);

                //Drop kmh from speed for type conversion
                String speed = runList.get(i).getSpeed();
                speed = speed.substring(0, speed.length()-4);
                speeds.add(speed);
            }

            //Calculate averages and bests of each category
            viewModel.calculateTimes(times);
            viewModel.calculateDistances(distances);
            viewModel.calculateSpeeds(speeds);

            //DataBinding within the LiveData observer seems to not work. If it did, the code below
            //would not be necessary and the value updates from calculations should have been
            //sufficient. This can be proved from how the text is simply set to viewmodel values.
            TextView avgTime = findViewById(R.id.atime);
            TextView avgDistance = findViewById(R.id.adistance);
            TextView avgSpeed = findViewById(R.id.aspeed);
            TextView bestTime = findViewById(R.id.btime);
            TextView bestDistance = findViewById(R.id.bdistance);
            TextView bestSpeed = findViewById(R.id.bspeed);
            TextView totalTime = findViewById(R.id.totalt);
            TextView totalDistance = findViewById(R.id.totald);
            TextView runs = findViewById(R.id.runs);

            avgTime.setText(viewModel.avgTime);
            avgDistance.setText(viewModel.avgDistance);
            avgSpeed.setText(viewModel.avgSpeed);
            bestTime.setText(viewModel.bestTime);
            bestDistance.setText(viewModel.bestDistance);
            bestSpeed.setText(viewModel.bestSpeed);
            totalTime.setText(viewModel.totalTime);
            totalDistance.setText(viewModel.totalDistance);
            runs.setText("Total Runs: "+viewModel.runs);
        });
    }
}