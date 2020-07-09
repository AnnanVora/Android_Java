package annan.example.tasktimer;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple timing object
 * Sets it's start timing when created and calculates how long since creation
 * when setDuration is called
 */
class Timing implements Serializable {
    private static final long serialVersionUID = 27082007L;
    private static final String TAG = Timing.class.getSimpleName();

    private long _ID;
    private Task task;
    private long startTime;
    private long duration;

    public Timing(Task task) {
        this.task = task;
        Date currentTime = new Date();
        this.startTime = currentTime.getTime() / 1000;
        this.duration = 0;
    }

    long getID() {
        return _ID;
    }

    void setID(long _ID) {
        this._ID = _ID;
    }

    Task getTask() {
        return task;
    }

    void setTask(Task task) {
        this.task = task;
    }

    long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    long getDuration() {
        return duration;
    }

    void setDuration() {
        Date currentTime = new Date();
        this.duration = (currentTime.getTime() / 1000) - startTime;
        Log.d(TAG, "setDuration: " + this.task.getID() + " - Start time " + this.startTime +
            " | Duration: " + this.duration);
    }
}
