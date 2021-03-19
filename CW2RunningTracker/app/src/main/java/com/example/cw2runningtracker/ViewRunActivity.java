package com.example.cw2runningtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

//ViewRunActivity to view a specific run on google maps
public class ViewRunActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {

    //Decla db, dao, viewmodel
    TrackerRoomDatabase db;
    RunningTrackerDao runningTrackerDao;
    ViewRunViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_run);

        //Instantiate viewmodel, db, dao
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(ViewRunViewModel.class);

        db = TrackerRoomDatabase.getDatabase(getApplicationContext());
        runningTrackerDao = db.runningTrackerDao();

        //Get id and coords of specific run from bundle
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        int id = bundle.getInt("id");
        viewModel.id = id;
        viewModel.coords = bundle.getString("coords");

        //Convert coords from String to List<LatLng> format
        viewModel.convertCoords(viewModel.coords);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //Delete current run being viewed
    public void deleteRun(View v){
        viewModel.deleteRun();
        finish();
    }

    //Method to draw the run route on the map and set the zoom of it
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Set list to list in viewmodel that was converted earlier
        List<LatLng> map = viewModel.getMap();

        //Set halfway to middle of array to then use it as halfway of run for camera
        int halfway = map.size() / 2;
        //Final point is final in array
        int terminal = map.size() -1;

        //For each entry in list, get its Lat and Lng and the next ones Lat and Lng and draw a Polyline
        //in between to recreate the run route
        for(int i = 0; i < map.size()-1; i++){
            LatLng start = map.get(i);
            LatLng end = map.get(i+1);

            //Polyline with width 20, color blue and clickable set to false as no onclick methods
            //are needed
            Polyline line = googleMap.addPolyline(new PolylineOptions().add(
                    new LatLng(start.latitude, start.longitude),
                    new LatLng(end.latitude, end.longitude)
            ).width(20).color(Color.BLUE).clickable(false)
            );

        }

        //Add markers to display start and end of route
        LatLng start = new LatLng(map.get(0).latitude, map.get(0).longitude);
        LatLng end = new LatLng(map.get(terminal).latitude, map.get(terminal).longitude);

        //Set titles to the markers
        googleMap.addMarker(new MarkerOptions().position(start).title("Start"));
        googleMap.addMarker(new MarkerOptions().position(end).title("End"));

        //Set camera on map to the "halfway" point in the list of locations
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.get(halfway).latitude,
                map.get(halfway).longitude), 15));
    }

    //No implementation for onClick needed
    @Override
    public void onPolylineClick(Polyline polyline) {

    }
}