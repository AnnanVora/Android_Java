package annan.example.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
    CursorRecyclerViewAdapter.OnTaskClickListener {

    public static final int LOADER_ID = 0;
    private static final String TAG = "MainActivityFragment";
    private CursorRecyclerViewAdapter adapter;
    private Timing currentTiming = null;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement CursorRecyclerViewAdapter.OnTaskClickListener");
        }
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        setTimingText(this.currentTiming);
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if (listener != null) {
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if (listener != null) {
            listener.onDeleteClick(task);
        }
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        Log.d(TAG, "onTaskLongClick: called");
        if (this.currentTiming != null) {
            if (task.getID() == this.currentTiming.getTask().getID()) {
                saveTiming(this.currentTiming);
                this.currentTiming = null;
                setTimingText(null);
            } else {
                saveTiming(this.currentTiming);
                this.currentTiming = new Timing(task);
                setTimingText(this.currentTiming);
            }
        } else {
            this.currentTiming = new Timing(task);
            setTimingText(this.currentTiming);
        }
    }

    private void saveTiming(@NonNull Timing currentTiming) {
        Log.d(TAG, "saveTiming: starts");
        currentTiming.setDuration();

        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.getTask().getID());
        values.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.getStartTime());
        values.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.getDuration());
        contentResolver.insert(TimingsContract.CONTENT_URI, values);

        Log.d(TAG, "saveTiming: ends");
    }

    private void setTimingText(Timing timing) {
        TextView taskName = getActivity().findViewById(R.id.current_task);
        if (timing != null) {
            taskName.setText(getString(R.string.current_timing_text, timing.getTask().getName()));
        } else {
            taskName.setText(R.string.no_task_message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (adapter == null) {
            adapter = new CursorRecyclerViewAdapter(null, this);
        }
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "onCreateView: returning");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Log.d(TAG, "onCreateLoader: starts with id " + id);
        String[] projection = {TasksContract.Columns._ID,
            TasksContract.Columns.TASKS_NAME,
            TasksContract.Columns.TASKS_DESCRIPTION,
            TasksContract.Columns.TASKS_SORTORDER};

        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + "," + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(), TasksContract.CONTENT_URI, projection, null, null, sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished: starts");
        adapter.swapCursor(data);
        int count = adapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count = " + count);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        adapter.swapCursor(null);
    }
}
