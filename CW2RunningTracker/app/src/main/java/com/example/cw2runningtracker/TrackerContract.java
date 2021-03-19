package com.example.cw2runningtracker;

import android.net.Uri;

//Code provided for coursework 3, adjusted to match data used in this application context
public class TrackerContract {
    //Authority
    public static final String AUTHORITY = "com.example.cw2runningtracker.TrackerProvider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    //Tables
    public static final String RUNS_TABLE = "tracker_table";
    public static final Uri RUNS_URI = Uri.parse("content://"+AUTHORITY+"/tracker_table/");

    //field names
    public static final String RUN_ID = "run_id";
    public static final String RUN_DATE = "date";
    public static final String RUN_TIME = "time";
    public static final String RUN_MESSAGE = "message";
    public static final String RUN_DISTANCE = "distance";
    public static final String RUN_SPEED = "speed";
    public static final String RUN_COORDINATES = "coords";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/RecipeProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/RecipeProvider.data.text";

}
