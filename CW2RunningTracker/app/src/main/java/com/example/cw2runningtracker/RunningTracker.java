package com.example.cw2runningtracker;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracker_table")
public class RunningTracker {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "run_id")
    private int _id;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    //Total time tracker was active
    @NonNull
    @ColumnInfo(name = "time")
    private String time;


    //User annotation
    @NonNull
    @ColumnInfo(name = "message")
    private String message;

    //Distance travelled
    @NonNull
    @ColumnInfo(name = "distance")
    private String distance;

    //Speed of run/walk
    @NonNull
    @ColumnInfo(name = "speed")
    private String speed;

    //Coordinates list within formatted string
    @NonNull
    @ColumnInfo(name = "coords")
    private String coords;

    //Constructor
    public RunningTracker(@NonNull String date,
                          @NonNull String time,
                          @NonNull String message,
                          @NonNull String distance,
                          @NonNull String speed,
                          @NonNull String coords){
        this.date = date;
        this.time = time;
        this.message = message;
        this.distance = distance;
        this.speed = speed;
        this.coords = coords;
    }

    //Getters and setters for above fields
    public void set_id(int _id){
        this._id = _id;
    }
    public int get_id(){ return _id; }

    @NonNull
    public String getDate(){ return date; }
    @NonNull
    public String getTime(){ return time; }
    @NonNull
    public String getMessage() { return message; }
    @NonNull
    public String getDistance() { return distance; }
    @NonNull
    public String getSpeed() { return speed; }
    @NonNull
    public String getCoords() { return coords; }
}
