package annan.example.tasktimer;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import annan.example.tasktimer.debug.TestData;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
    AddEditActivityFragment.OnSaveClicked,
    AppDialog.DialogEvents {

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;
    private static final int DIALOG_ID_CANCEL_EDIT_UP = 3;
    private static final String TAG = "MainActivity";
    private static final String TASK_ID_STRING = "TaskID";
    private boolean isTwoPane = false;
    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isTwoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: isTwoPane is " + isTwoPane);

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean editing = fragmentManager.findFragmentById(R.id.task_details_container) != null;
        Log.d(TAG, "onCreate: editing = " + editing);

        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);
//        FIXME
        findViewById(R.id.menumain_settings).setVisibility(View.INVISIBLE);

        if (isTwoPane) {
            Log.d(TAG, "onCreate: twoPaneMode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        } else if (editing) {
            Log.d(TAG, "onCreate: singlePaneEditing");
            mainFragment.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onCreate: singlePaneAdding");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_details_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);

        if (!isTwoPane) {
            addEditLayout.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (BuildConfig.DEBUG) {
            MenuItem gen = menu.findItem(R.id.menumain_generate);
            gen.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menumain_addTask:
                taskEditAddRequest(null);
                break;
            case R.id.menumain_showDurations:
                startActivity(new Intent(this, DurationsReportActivity.class));
                break;
            case R.id.menumain_settings:
                Log.d(TAG, "onOptionsItemSelected: Not implemented; Stub!");
                break;
            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.task_details_container);
                if (fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void showAboutDialog() {
        @SuppressLint("InflateParams") View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MainActivity.this.dialog != null && MainActivity.this.dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        TextView tv = messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        TextView about_url = messageView.findViewById(R.id.about_url);
        if (about_url != null) {
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) v).getText().toString();
                    intent.setData(Uri.parse(s));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No browser application found, cannot visit world-wide-web", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        dialog.show();
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditAddRequest(task);
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.getID(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);
        args.putLong(TASK_ID_STRING, task.getID());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void taskEditAddRequest(@Nullable Task task) {

        AddEditActivityFragment fragment = new AddEditActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable(Task.class.getSimpleName(), task);
        fragment.setArguments(args);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.task_details_container, fragment)
            .commit();

        if (!isTwoPane) {
            Log.d(TAG, "taskEditAddRequest: in single-pane mode (phone)");
            View mainFragment = findViewById(R.id.fragment);
            View addEditLayout = findViewById(R.id.task_details_container);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "taskEditAddRequest: exiting");
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch (dialogID) {
            case DIALOG_ID_DELETE:
                long taskID = args.getLong(TASK_ID_STRING);
                if (BuildConfig.DEBUG && taskID == 0) throw new AssertionError("Task ID is 0");
                getContentResolver().delete(TasksContract.buildTaskUri(taskID), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
                break;
            case DIALOG_ID_CANCEL_EDIT_UP:
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogID, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogID) {
            case DIALOG_ID_DELETE:
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                    if (isTwoPane) {
                        if (dialogID == DIALOG_ID_CANCEL_EDIT) {
                            finish();
                        }
                    } else {
                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.fragment);

                        addEditLayout.setVisibility(View.GONE);
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDialogCancel(int dialogID) {
        Log.d(TAG, "onDialogCancel: called");
        Log.d(TAG, "onDialogCancel: Stub!");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if (fragment == null || fragment.canClose()) {
            super.onBackPressed();
        } else {
            showConfirmationDialog(DIALOG_ID_CANCEL_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showConfirmationDialog(int dialogID) {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDialogMessage));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDialog_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDialog_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
//      Required to satisfy interface
        Log.d(TAG, "onTaskLongClick: Stub!");
    }
}
