package com.example.cw2runningtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cw2runningtracker.databinding.ActivityAddRunBinding;

import java.text.DecimalFormat;

//AddRunActivity that allows user to save their runs once they complete it. This activity also
//provides the option to add a custom annotation to describe the run
public class AddRunActivity extends AppCompatActivity {

    //Declare viewmodel, dao and db
    private AddRunViewModel viewModel;
    RunningTrackerDao runningTrackerDao;
    TrackerRoomDatabase db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bind data
        ActivityAddRunBinding v = ActivityAddRunBinding.inflate(LayoutInflater.from(this));
        setContentView(v.getRoot());

        //Initialise viewmodel, dao and db
        db = TrackerRoomDatabase.getDatabase(getApplicationContext());
        runningTrackerDao = db.runningTrackerDao();

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AddRunViewModel.class);
        v.setViewmodel(viewModel);

        //Get run data from bundle
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String coords = bundle.getString("coords");
        String start = bundle.getString("start");
        String end = bundle.getString("end");
        int time = bundle.getInt("time");

        //Calculate time to convert from seconds into HH:MM:SS format
        String timeString = viewModel.calculateTime(time);

        //Update UI with data binding
        viewModel.start = start;
        viewModel.end = end;
        viewModel.time = timeString;
        viewModel.coords = coords;
        viewModel.distance = viewModel.calculateDistance(coords);
        viewModel.speed = viewModel.calculateSpeed(time);
    }

    //onClick method to save current run with displayed data and finish
    public void addRun(View v){
        //Get message value
        EditText emessage = (EditText) findViewById(R.id.tmessage);
        String message = emessage.getText().toString();

        //Create new RunningTracker object to insert into database
        RunningTracker runningTracker = new RunningTracker(viewModel.start,viewModel.time,
                message, viewModel.distance, viewModel.speed, viewModel.coords);
        viewModel.insert(runningTracker);

        //Finish
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    //Cancel adding run and finish (onClick)
    public void cancel(View v){
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}