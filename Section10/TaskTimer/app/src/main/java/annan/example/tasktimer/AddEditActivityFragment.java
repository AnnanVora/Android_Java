package annan.example.tasktimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AddEditActivityFragment extends Fragment {

    private static final String TAG = "AddEditActivityFragment";
    private FragmentEditMode mode;
    private EditText nameTextView;
    private EditText descriptionTextView;
    private EditText sortOrderTextView;
    private OnSaveClicked saveListener = null;

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: Constructor called");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement AddEditActivity.OnSaveClicked");
        }
        saveListener = (OnSaveClicked) activity;
    }

    @Nullable
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        nameTextView = view.findViewById(R.id.addedit_name);
        descriptionTextView = view.findViewById(R.id.addedit_description);
        sortOrderTextView = view.findViewById(R.id.addedit_sortOrder);
        Button saveButton = view.findViewById(R.id.addedit_save);

        Bundle args = getArguments();

        final Task task;
        if (args != null) {
            Log.d(TAG, "onCreateView: retrieving task details");
            task = (Task) args.getSerializable(Task.class.getSimpleName());
            if (task != null) {
                Log.d(TAG, "onCreateView: Task details found, editing...");
                nameTextView.setText(task.getName());
                descriptionTextView.setText(task.getDescription());
                sortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mode = FragmentEditMode.EDIT;
            } else {
                mode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            Log.d(TAG, "onCreateView: No args, adding new record");
            mode = FragmentEditMode.ADD;
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int so;
                if (sortOrderTextView.length() > 0) {
                    so = Integer.parseInt(sortOrderTextView.getText().toString());
                } else {
                    so = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mode) {
                    case EDIT:
                        if (task == null) {
                            // Remove lint warning
                            break;
                        }
                        if (!nameTextView.getText().toString().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, nameTextView.getText().toString());
                        }
                        if (!descriptionTextView.getText().toString().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, descriptionTextView.getText().toString());
                        }
                        if (so != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                        }
                        if (values.size() != 0) {
                            Log.d(TAG, "onClick: updating tasks");
                            contentResolver.update(TasksContract.buildTaskUri(task.getID()), values, null, null);
                        }
                        break;
                    case ADD:
                        if (nameTextView.length() > 0) {
                            Log.d(TAG, "onClick: adding new task");
                            values.put(TasksContract.Columns.TASKS_NAME, nameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, descriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TasksContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: done editing");

                if (saveListener != null) {
                    saveListener.onSaveClicked();
                }
                Log.d(TAG, "onClick: exiting");
            }
        });
        Log.d(TAG, "onCreateView: exiting");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        saveListener = null;
    }

    public boolean canClose() {
        return false;
    }

    private enum FragmentEditMode {
        EDIT,
        ADD
    }

    interface OnSaveClicked {
        void onSaveClicked();
    }
}