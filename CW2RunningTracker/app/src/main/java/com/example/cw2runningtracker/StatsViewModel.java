package com.example.cw2runningtracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StatsViewModel extends AndroidViewModel {

    //Declare variables for UI
    public String avgTime;
    public String avgDistance;
    public String avgSpeed;
    public String bestTime;
    public String bestDistance;
    public String bestSpeed;
    public String totalTime;
    public String totalDistance;
    public String runs;

    //Format to avoid excess decimals
    private static DecimalFormat decimalFormat = new DecimalFormat("#.####");

    //Declare repository and LiveData List for all runs
    private TrackerRepository trackerRepository;
    public LiveData<List<RunningTracker>> allRuns;

    //Constructor to initialise the repository and livedata
    public StatsViewModel(@NonNull Application application) {
        super(application);
        trackerRepository = new TrackerRepository(application);
        allRuns = trackerRepository.getAllRuns();
    }

    //Get all runs
    public LiveData<List<RunningTracker>> getAllRuns(){ return allRuns;}

    //Convert time from seconds into HH:MM:SS
    public String convertTime(int time){
        Date d = new Date(time * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String output = df.format(d);
        return output;
    }

    //From list of times, get average, best and total
    public void calculateTimes(List<String> times){
        int average = 0;
        runs = Integer.toString(times.size());
        int best = 0;
        for(int i = 0; i < times.size(); i ++){
            int temp = Integer.parseInt(times.get(i));
            //Check for new best
            if(temp > best){
                best = temp;
            }
            average += temp;
        }
        totalTime = "Total Run Time: "+convertTime(average);
        bestTime = "Longest Run Time: "+convertTime(best);
        avgTime = "Average Run Time: "+convertTime(average / Integer.parseInt(runs));
    }

    //From list of distances, get average, best and total
    public void calculateDistances(List<String> distances){
        double average = 0;
        double best = 0;
        for(int i = 0; i < distances.size(); i ++){
            double temp = Double.parseDouble(distances.get(i).toString());
            average += temp;
            if(temp > best){
                best = temp;
            }
        }
        totalDistance = "Total Distance Ran: "+Double.parseDouble(decimalFormat.format(average))+"m";
        bestDistance = "Best Run Distance: "+Double.parseDouble(decimalFormat.format(best))+"m";
        avgDistance = "Average Run Distance: "+Double.parseDouble(decimalFormat.format(average / Integer.parseInt(runs)))+"m";
    }

    //From list of speeds, get average and best
    public void calculateSpeeds(List<String> speeds){
        double average = 0;
        double best = 0;
        for(int i = 0; i < speeds.size(); i ++){
            double temp = Double.parseDouble(speeds.get(i));
            average += temp;
            if(temp > best){
                best = temp;
            }
        }
        bestSpeed = "Fastest Run Speed: "+Double.parseDouble(decimalFormat.format(best))+"kmh";
        avgSpeed = "Average Run Speed: "+Double.parseDouble(decimalFormat.format(average / Integer.parseInt(runs)))+"kmh";
    }
}
