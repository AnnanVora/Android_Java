package annan.example.tasktimer.debug;

public class TestTiming {

    long taskID;
    long startTime;
    long duration;

    public TestTiming(long taskID, long startTime, long duration) {
        this.taskID = taskID;
        this.startTime = startTime / 1000;
        this.duration = duration;
    }
}
