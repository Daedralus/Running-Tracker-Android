package com.example.cw2runningtracker;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//RunningTrackerDao where queries can be made to retrieve specific RunningTracker instances.
//Individual functions are self-explanatory from their names and types (as well as SQL statements)
@Dao
public interface RunningTrackerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RunningTracker runningTracker);

    @Query("DELETE FROM tracker_table")
    void deleteAll();

    @Query("SELECT * FROM tracker_table")
    LiveData<List<RunningTracker>> getAll();

    @Query("SELECT * FROM tracker_table WHERE run_id = :id")
    LiveData<RunningTracker> getRunById(int id);

    @Query("DELETE FROM tracker_table WHERE run_id = :id")
    void deleteRun(int id);

    @Query("SELECT * FROM tracker_table")
    Cursor getAllRunsCursor();

    @Query("SELECT * FROM tracker_table WHERE run_id = :id")
    Cursor getAllRunsByIdCursor(int id);

    @Query("SELECT * FROM tracker_table WHERE date = :date")
    Cursor getAllRunsByDateCursor(String date);

    @Query("SELECT * FROM tracker_table WHERE time = :time")
    Cursor getAllRunsByTimeCursor(String time);

    @Query("SELECT * FROM tracker_table WHERE message = :message")
    Cursor getAllRunsByMessageCursor(String message);

    @Query("SELECT * FROM tracker_table WHERE distance = :distance")
    Cursor getAllRunsByDistanceCursor(String distance);

    @Query("SELECT * FROM tracker_table WHERE speed = :speed")
    Cursor getAllRunsBySpeedCursor(String speed);

    @Query("SELECT * FROM tracker_table WHERE coords = :coords")
    Cursor getAllRunsByCoordinatesCursor(String coords);
}
