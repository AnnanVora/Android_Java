package annan.example.tasktimer;

import androidx.annotation.NonNull;

import java.io.Serializable;

class Task implements Serializable {

    public static final long serialVersionUID = 20161120L;

    private long _id;
    private final String name;
    private final String description;
    private final int sortOrder;

    public Task(long id, String name, String description, int sortOrder) {
        this._id = id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setID(long id) {
        this._id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Task {" +
            "_id = " + _id +
            ", name = " + name +
            ", description = " + description +
            ", sortOrder = " + sortOrder +
            "}";
    }
}