package annan.example.tasktimer;

import androidx.annotation.NonNull;

import java.io.Serializable;

class Task implements Serializable {

    public static final long serialVersionUID = 20161120L;
    private final String name;
    private final String description;
    private final int sortOrder;
    private long _id;

    Task(long id, String name, String description, int sortOrder) {
        this._id = id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    long getID() {
        return _id;
    }

    void setID(long id) {
        this._id = id;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    int getSortOrder() {
        return sortOrder;
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
