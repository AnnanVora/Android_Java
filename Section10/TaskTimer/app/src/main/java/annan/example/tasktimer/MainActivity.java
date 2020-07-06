package annan.example.tasktimer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements  CursorRecyclerViewAdapter.OnTaskClickListener,
                                                               AddEditActivityFragment.OnSaveClicked {

    private static final String TAG = "MainActivity";
    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";
    private boolean isTwoPane = false;
    public static final int DELETE_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.task_details_container) != null) {
            isTwoPane = true;
        }
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_details_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menumain_addTask:
                taskEditAddRequest(null);
            case R.id.menumain_showDurations:
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Task task) {
        taskEditAddRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.get_id(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
        getContentResolver().delete(TasksContract.buildTaskUri(task.get_id()), null, null);
    }

    private void taskEditAddRequest(@Nullable Task task) {
        if (isTwoPane) {
            Log.d(TAG, "taskEditAddRequest: in two-pane mode (tablet)");

            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle args = new Bundle();
            args.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(args);

            getSupportFragmentManager() .beginTransaction()
                                        .replace(R.id.task_details_container, fragment)
                                        .commit();
        } else {
            Log.d(TAG, "taskEditAddRequest: in single-pane mode (phone)");
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) {
                detailIntent.putExtra(Task.class.getSimpleName(), task);
            }
            startActivity(detailIntent);
        }
    }
}