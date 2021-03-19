package com.example.cw2runningtracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

//RunningTrackerAdapter class based on MartinRoomMVVM AnimalAdapter
public class RunningTrackerAdapter extends RecyclerView.Adapter<RunningTrackerAdapter.RunningTrackerViewHolder> {

    //This additional variable is needed for being able to click on specific RunningTrackers
    private onClickListener mOnClickListen;
    private List<RunningTracker> data;
    private Context context;
    private LayoutInflater layoutInflater;

    //Instantiate variables in constructor
    public RunningTrackerAdapter(Context context, onClickListener onClickListener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mOnClickListen = onClickListener;
    }

    //RunningTrackerViewHolder now also returns an "onClickListener" to adapt to additions
    @Override
    public RunningTrackerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.database_layout, parent, false);
        return new RunningTrackerViewHolder(itemView, mOnClickListen);
    }

    @Override
    public void onBindViewHolder(RunningTrackerViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<RunningTracker> newData) {
        if (data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    //Added method to be able to retrieve the data in List<> format
    public List<RunningTracker> getData(){
        if(data != null){
            return data;
        }else {
            List<RunningTracker> blank = null;
            return blank;
        }
    }

    //RunningTrackerViewHolder implements OnClickListener interface so that the onclick can be invoked
    class RunningTrackerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dateView;
        TextView timeView;
        TextView messageView;
        TextView combinedView;
        onClickListener onClickListener;

        //Constructor is given the onClickListener to adapt to additions
        RunningTrackerViewHolder(View itemView, onClickListener onClickListener) {
            super(itemView);
            //OnClickListener is set to this instance
            this.onClickListener = onClickListener;

            dateView = itemView.findViewById(R.id.dateView);
            timeView = itemView.findViewById(R.id.timeView);
            messageView = itemView.findViewById(R.id.messageView);
            combinedView = itemView.findViewById(R.id.combinedView); //Combined distance/speed

            //The nameview is then listened so that if a RunningTracker name is clicked, it will call
            //the onRunningTrackerClick function.
            dateView.setOnClickListener(this);
            timeView.setOnClickListener(this);
            messageView.setOnClickListener(this);
            combinedView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bind(final RunningTracker RunningTracker) {

            if (RunningTracker != null) {
                dateView.setText("Date of Run: "+RunningTracker.getDate());
                timeView.setText("Duration of Run: "+RunningTracker.getTime());
                messageView.setText("Custom Message: "+RunningTracker.getMessage());
                combinedView.setText("Distance and Speed of Run: "+RunningTracker.getDistance()+", "+RunningTracker.getSpeed());
            }
        }

        //The onClick will get the position of the click within the adapter
        @Override
        public void onClick(View v) {
            //onClickListener.onRunningTrackerClick(getAdapterPosition());
            onClickListener.onRunningTrackerClick(getLayoutPosition());
        }
    }

    //Method that is overriden in MainActivity to retrieve the position of the click in the adapter
    public interface onClickListener{
        void onRunningTrackerClick(int position);
    }
}
