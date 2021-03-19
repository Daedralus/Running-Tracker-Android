package com.example.cw2runningtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.cw2runningtracker.databinding.ActivityAddRunBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//MainActivity which handles location service start/stop and displays all saved runs in recyclerview
public class MainActivity extends AppCompatActivity implements RunningTrackerAdapter.onClickListener {

    //Activity requests
    private static final int REQUEST_RUN = 1;
    private static final int REQUEST_NEW_RUN = 2;
    private static final int REQUEST_STATS = 3;

    //Declare Location related variables
    private LocationService.MyLocationListener myService = null;
    public static final int LOCATION_REQUEST_CODE = 1001; //Any number
    private LocationManager locationManager;
    private Location location;

    //Declare variables for db, dao, adapter, viewmodel and data handling list
    private List<RunningTracker> runs = new ArrayList<>();
    private RunningTrackerAdapter adapter;
    private TrackerRoomDatabase db;
    private RunningTrackerDao runningTrackerDao;
    private MainViewModel viewModel;

    //Bools for states
    private boolean running = true;
    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise locationmanager and service
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myService = new LocationService.MyLocationListener(LocationManager.GPS_PROVIDER);

        //Bind service
        this.bindService(new Intent(this, LocationService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);

        //Permission check is required for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER
                    ,1000*60,2, myService);
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        }

        //In case there was a config change, do not reset the running check
        if(savedInstanceState != null){
            running = savedInstanceState.getBoolean("running");
        }

        //Initialise db, dao and adapter
        db = TrackerRoomDatabase.getDatabase(getApplicationContext());
        runningTrackerDao = db.runningTrackerDao();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //This adapter also has an onClickListener parameter due to additions to the RunningTrackerAdapter class
        adapter = new RunningTrackerAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialise viewmodel and set it to observe the LiveData of runs
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MainViewModel.class);

        viewModel.getAllRuns().observe(this, runs ->{
            adapter.setData(runs);
        });
    }


    //Toggle a run, start as running = true so that first entry begins location updates
    @SuppressLint("SetTextI18n")
    public void toggleRun(View v){
        //Get service
        Intent serviceIntent = new Intent(MainActivity.this, LocationService.class);

        //bool first ensures that there is no run present
        if(first){
            //Get the date right now to set as start of run
            Date start = new Date();
            start.getTime();
            viewModel.setStartTime(start);

            //Flag first to false to prevent re-call and re-creation of start date and the service
            first = false;

            //Clear contents of location list then start service
            myService.clearLocations();
            serviceIntent.setAction("BEGIN TRACKING");
            startService(serviceIntent);
        }

        //Button to update text of
        Button button = (Button) findViewById(R.id.begin);

        //Logic depends on if run is paused or not
        if(!running){
            //If pausing, remove location updates and update button text
            locationManager.removeUpdates(myService);
            running = true;
            button.setText("Continue Run");
        }else{
            //If continuing, re-request location updates and update button text
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5, // minimum time interval between updates
                        5, // minimum distance between updates, in metres
                        myService);

            } catch(SecurityException e) {
                Log.d("logs", e.toString());
            }
            running = false;
            button.setText("Pause Run");
        }
    }

    //End tracking and prompt user to save their run
    public void endRun(View v){
        //Can't end a run that hasn't started
        if(first){
            return;
        }
        //Reset button to Begin as there is not a run anymore
        Button button = (Button) findViewById(R.id.begin);
        button.setText("Begin Run");
        //Reset flags for correct future states
        running = true;
        first = true;

        //Create new date at the end of a run to know when the run has ended
        Date end = new Date();
        end.getTime();
        viewModel.setEndTime(end);

        //Stop service updates and return final location list from service
        locationManager.removeUpdates(myService);
        List<Location> locations = myService.getLocationList();
        viewModel.locations = locations;


        //Put all locations into formatted string for easy read/write
        String coordList = "";
        for(int i = 0; i < locations.size(); i++){
            //With this format, individual pairs can be found from splitting by `:` and singular values from `,`
            if(i == locations.size()-1){
                //Final entry should not have : or might have null error later
                coordList = coordList + locations.get(i).getLatitude() + ", " + locations.get(i).getLongitude();
            }else{
                coordList = coordList + locations.get(i).getLatitude() + ", " + locations.get(i).getLongitude() + ":";
            }
        }

        //Get seconds elapsed between start and end time
        long timeDiff = viewModel.endTime.getTime() - viewModel.startTime.getTime();
        int seconds = (int)(timeDiff / 1000);

        //Start AddRunActivity to view data, add annotation (optional) then save data (or cancel)
        Intent intent = new Intent(MainActivity.this, AddRunActivity.class);
        Bundle bundle = new Bundle();
        //Data needed to create RunninTracker
        bundle.putString("coords", coordList);
        bundle.putInt("time", seconds);
        bundle.putString("start", viewModel.startTime.toString());
        bundle.putString("end", viewModel.endTime.toString());
        //Clear location list for next run
        myService.clearLocations();

        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_NEW_RUN);
    }

    //Start StatsActivity to view statistics throughout saved runs
    public void stats(View v){
        Intent intent = new Intent(MainActivity.this, StatsActivity.class);
        startActivityForResult(intent, REQUEST_STATS);
    }

    //Method that is triggered when an item on the recyclerview is clicked
    @Override
    public void onRunningTrackerClick(int position){
        //Get the specific run at click location
        runs = adapter.getData();
        int id = runs.get(position).get_id();
        String coords = runs.get(position).getCoords();

        //Pass specific run id and coords to ViewRunActivity and start it
        Intent intent = new Intent(this, ViewRunActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        bundle.putString("coords",coords);

        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_RUN);
    }

    //Save data in case of config change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("running", running);
    }

    //Restore data from save
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Restore values on ViewModel
        running = savedInstanceState.getBoolean("running");
    }

    //Bound service methods
    @Override
    protected void onStart() {
        super.onStart();
        // Bind service
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = (LocationService.MyLocationListener) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    };
}
