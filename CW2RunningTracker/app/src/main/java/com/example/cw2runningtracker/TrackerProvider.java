package com.example.cw2runningtracker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//ContentProvider built on top of MartinProvider demo
public class TrackerProvider extends ContentProvider {

    //UriMatcher where all uri's in use will be added for external calls
    private static final UriMatcher uriMatcher;
    //Declare the DAO's
    private RunningTrackerDao runningTrackerDao;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TrackerContract.AUTHORITY, TrackerContract.RUNS_TABLE, 1);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/id", 2);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/date", 3);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/time", 4);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/messsage", 5);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/distance", 6);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/speed", 7);
        uriMatcher.addURI(TrackerContract.AUTHORITY, "tracker_table/coords", 8);
    }
    //Initialise dao
    @Override
    public boolean onCreate() {
        runningTrackerDao = TrackerRoomDatabase.getDatabase(getContext()).runningTrackerDao();
        return true;
    }

    //Queries in switch statement to match URI
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)){
            case 1:
                //Get all runs
                return runningTrackerDao.getAllRunsCursor();
            case 2:
                //Get all runs with matching id
                int id = Integer.parseInt(uri.getLastPathSegment());
                return runningTrackerDao.getAllRunsByIdCursor(id);
            case 3:
                //get all runs with matching date
                String date = uri.getLastPathSegment();
                return runningTrackerDao.getAllRunsByDateCursor(date);
            case 4:
                //Get all runs with matching time
                String time = uri.getLastPathSegment();
                return runningTrackerDao.getAllRunsByTimeCursor(time);
            case 5:
                //Get all runs with matching message
                String message = uri.getLastPathSegment();
                return runningTrackerDao.getAllRunsByMessageCursor(message);
            case 6:
                //Get all runs with matching distance
                String distance = uri.getLastPathSegment();
                return runningTrackerDao.getAllRunsByDistanceCursor(distance);
            case 7:
                //Get all runs with matching speed
                String speed = uri.getLastPathSegment();
                return runningTrackerDao.getAllRunsBySpeedCursor(speed);
            case 8:
                //Get all runs with matching coordinates
                String coords = uri.getLastPathSegment();
                return runningTrackerDao.getAllRunsByCoordinatesCursor(coords);
        }
        //Failed uri
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String contentType;

        if (uri.getLastPathSegment()==null) {
            contentType = TrackerContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = TrackerContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }

    //Keeping external data access to read only so the following write methods are not implemented
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
