package com.example.cw2runningtracker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cw2runningtracker.RunningTracker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TrackerRoomDatabase based on MartivRoomMVVM "MyRoomDatabase" Demo
@Database(entities = {RunningTracker.class}, version = 15, exportSchema = false)
public abstract class TrackerRoomDatabase extends RoomDatabase{

    public abstract RunningTrackerDao runningTrackerDao();

    private static volatile TrackerRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TrackerRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrackerRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrackerRoomDatabase.class, "runningtracker_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(createCallback)
                            //allowMainThreadQueries()
                            .build();
                }

            }
        }
        return INSTANCE;
    }

    //Callback that will insert a run log manually on the onCreate of db
    private static RoomDatabase.Callback createCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                RunningTrackerDao dao = INSTANCE.runningTrackerDao();
                dao.deleteAll();

                RunningTracker runningTracker = new RunningTracker("Fri Dec 20 20:20:20 GMT 2020", "00:07:00",
                        "Sample Run", "643.738m","5.517754kmh", "52.9378, -1.1942283333333332:52.93769833333334, -1.1942683333333333:52.93769833333334, -1.1941199999999998:52.9378, -1.1939899999999999:52.93789833333334, -1.1938583333333335:52.93789833333334, -1.19374:52.937999999999995, -1.1936283333333333:52.937999999999995, -1.1934883333333335:52.93809833333333, -1.1933583333333333:52.93820000000001, -1.1932483333333332:52.93820000000001, -1.1931083333333334:52.93829833333333, -1.1929883333333333:52.93829833333333, -1.1928483333333333:52.9384, -1.1927083333333333:52.938498333333335, -1.1925999999999999:52.938498333333335, -1.1924983333333332:52.938599999999994, -1.19239:52.938599999999994, -1.19226:52.938698333333335, -1.1921383333333333:52.93880000000001, -1.19201:52.93880000000001, -1.1918583333333332:52.93889833333333, -1.1917283333333333:52.939, -1.1915983333333333:52.939, -1.19147:52.93909999999999, -1.1913183333333333:52.93909999999999, -1.1911883333333333:52.93919833333333, -1.1910483333333335:52.93919833333333, -1.1909299999999998:52.9393, -1.1908083333333332:52.9393, -1.19069:52.93939833333334, -1.1905483333333333:52.93939833333334, -1.1904299999999999:52.939499999999995, -1.19029:52.939598333333336, -1.19015:52.939598333333336, -1.19001:52.939598333333336, -1.1898683333333333:52.939499999999995, -1.18974:52.939499999999995, -1.1896183333333332:52.93939833333334, -1.1895283333333335:52.93939833333334, -1.1893883333333333:52.9393, -1.1892483333333335:52.93919833333333, -1.1891483333333333:52.93909999999999, -1.18906:52.939, -1.1890083333333334:52.93889833333333, -1.1888083333333335:52.93880000000001, -1.18869:52.938698333333335, -1.1885683333333332:52.938599999999994, -1.1884783333333333:52.938599999999994, -1.1883783333333333:52.938498333333335, -1.18828:52.9384, -1.18818:52.93829833333333, -1.18808:52.93829833333333, -1.1879983333333333:52.93820000000001, -1.18792:52.93809833333333, -1.18784:52.937999999999995, -1.1877583333333335:52.937999999999995, -1.18767:52.93789833333334, -1.18757:52.9378, -1.18751:52.93769833333334, -1.18751:52.9376, -1.18758:52.9376, -1.18768");
                dao.insert(runningTracker);

            });
        }
    };
}
