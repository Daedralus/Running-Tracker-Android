package com.example.cw2runningtracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {

    //Variables needed for notification
    private final String CHANNEL_ID = "1";
    int NOTIFICATION_ID = 001;
    private static final int FOREGROUND_ID=1338;

    @Override
    public void onCreate(){ super.onCreate();}

    //Method called when service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //If stop action was triggered from notification, stop foreground service
        if(intent.getAction().equals("STOP")){
            stopForegroundService();
        }else{
            //Otherwise, start foreground service and build notification for it
            startForeground(FOREGROUND_ID, buildForegroundNotification());
        }
        return Service.START_STICKY;
    }

    //Bind service to activity
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyLocationListener(LocationManager.GPS_PROVIDER);
    }

    //LocationListener code provided in coursework. Extends binder to adapt to architecture
    public static class MyLocationListener extends Binder implements LocationListener {
        Location mLastLocation;
        //List of locations is gathered in service then returned to activity
        List<Location> locations = new ArrayList<>();

        public MyLocationListener(String provider) {
            Log.d("logs", "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d("logs", "new loc: "+location.getLatitude() + " " + location.getLongitude());
            addLocation(location); //Add new location to list
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // information about the signal, i.e. number of satellites
            Log.d("logs", "onStatusChanged: " + provider + " " + status);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("logs", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            Log.d("logs", "onProviderDisabled: " + provider);
        }

        //Add location to list
        public void addLocation(Location location){ locations.add(location);}

        //Return location list
        public List<Location> getLocationList(){
            return locations;
        }

        //Empty location list
        public void clearLocations(){ locations.clear(); }
    }

    //Method to remove notification manually
    public void removeNotification(){
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    //Method to build notification, built on top of code from lab 3
    private Notification buildForegroundNotification(){

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        //Set pending intent with action stop to be able to handle a stop button to stop service
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //Pending intent to allow notification actions
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction("STOP");

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action stop = new NotificationCompat.Action(R.drawable.ic_launcher_foreground, "STOP", pendingIntent);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("RunningTracker")
                .setContentText("RunningTracker is active and is using your location")
                .setContentIntent(contentIntent)
                .addAction(stop)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return mBuilder.build();
    }

    //Stop foreground service and remove notification
    public void stopForegroundService(){
        stopForeground(true);
        stopSelf();
    }


}
