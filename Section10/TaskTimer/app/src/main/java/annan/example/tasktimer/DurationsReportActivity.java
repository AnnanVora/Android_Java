package annan.example.tasktimer;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationsReportActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor>,
    DatePickerDialog.OnDateSetListener,
    AppDialog.DialogEvents,
    View.OnClickListener {

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";
    public static final String DELETION_DATE = "DELETION_DATE";
    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;
    private static final int LOADER_ID = 1;
    private static final String TAG = "DurationsReportActivity";
    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";
    private final GregorianCalendar calendar = new GregorianCalendar();
    private boolean displayWeek = true;
    private Bundle args = new Bundle();
    private DurationsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durations_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            long timeInMillis = savedInstanceState.getLong(CURRENT_DATE);
            if (timeInMillis != 0) {
                calendar.setTimeInMillis(timeInMillis);
                calendar.clear(GregorianCalendar.HOUR_OF_DAY);
                calendar.clear(GregorianCalendar.MINUTE);
                calendar.clear(GregorianCalendar.SECOND);
            }
            displayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK, true);
        }
        applyFilter();

        findViewById(R.id.td_name_heading).setOnClickListener(this);
        if (findViewById(R.id.td_description_heading) != null) {
            findViewById(R.id.td_description_heading).setOnClickListener(this);
        }
        findViewById(R.id.td_start_heading).setOnClickListener(this);
        findViewById(R.id.td_duration_heading).setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (this.adapter == null) {
            this.adapter = new DurationsRVAdapter(this, null);
        }
        recyclerView.setAdapter(adapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, this.args, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                String[] projection = {BaseColumns._ID,
                    DurationsContract.Columns.DURATIONS_NAME,
                    DurationsContract.Columns.DURATIONS_DESCRIPTION,
                    DurationsContract.Columns.DURATIONS_START_TIME,
                    DurationsContract.Columns.DURATIONS_START_DATE,
                    DurationsContract.Columns.DURATIONS_DURATION};
                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if (args != null) {
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }

                return new CursorLoader(this,
                    DurationsContract.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rm_filter_period:
                this.displayWeek = !this.displayWeek;
                applyFilter();
                invalidateOptionsMenu();
                LoaderManager.getInstance(this).initLoader(LOADER_ID, this.args, this);
                return true;
            case R.id.rm_filter_date:
                showDatePickerDialog(getString(R.string.date_title_filter), DIALOG_FILTER);
            case R.id.rm_delete:
                showDatePickerDialog(getString(R.string.date_title_delete), DIALOG_DELETE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog(String title, int dialogID) {
        Log.d(TAG, "showDatePickerDialog: starts");
        DialogFragment dialogFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.DATE_PICKER_ID, dialogID);
        args.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        args.putSerializable(DatePickerFragment.DATE_PICKER_DATE, this.calendar.getTime());

        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
        Log.d(TAG, "showDatePickerDialog: ends");
    }

    private void applyFilter() {
        Log.d(TAG, "applyFilter: starts");
        if (this.displayWeek) {
            Date currentCalDate = this.calendar.getTime();
            int dayOfWeek = this.calendar.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = this.calendar.getFirstDayOfWeek();
            Log.d(TAG, "applyFilter: first day of calendar week id " + weekStart);
            Log.d(TAG, "applyFilter: dayOfWeek = " + dayOfWeek);
            Log.d(TAG, "applyFilter: date is " + currentCalDate);

            this.calendar.set(GregorianCalendar.DAY_OF_WEEK, weekStart);
            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                this.calendar.get(GregorianCalendar.YEAR),
                this.calendar.get(GregorianCalendar.MONTH) + 1,
                this.calendar.get(GregorianCalendar.DAY_OF_MONTH));

            this.calendar.add(GregorianCalendar.DATE, 6);

            String endDate = String.format(Locale.US, "%04d-%02d-%02d",
                this.calendar.get(GregorianCalendar.YEAR),
                this.calendar.get(GregorianCalendar.MONTH) + 1,
                this.calendar.get(GregorianCalendar.DAY_OF_MONTH));

            String[] selectionArgs = new String[]{startDate, endDate};
            this.calendar.setTime(currentCalDate);
            Log.d(TAG, "applyFilter: Start/End date is " + startDate + ", " + endDate);

            this.args.putString(SELECTION_PARAM, "StartDate Between ? AND ?");
            this.args.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        } else {
            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                this.calendar.get(GregorianCalendar.YEAR),
                this.calendar.get(GregorianCalendar.MONTH) + 1,
                this.calendar.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate};
            Log.d(TAG, "applyFilter: startDay = " + startDate);
            this.args.putString(SELECTION_PARAM, "StartDate = ?");
            this.args.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.rm_filter_period);
        if (item != null) {
            if (this.displayWeek) {
                item.setIcon(R.drawable.filter_1);
                item.setTitle(R.string.rm_title_filter_day);
            } else {
                item.setIcon(R.drawable.filter_7);
                item.setTitle(R.string.rm_title_filter_date);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: starts");

        int dialogID = (int) view.getTag();
        this.calendar.set(year, month, dayOfMonth);
        switch (dialogID) {
            case DIALOG_FILTER:
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, this.args, this);
                break;
            case DIALOG_DELETE:
                String fromDate = DateFormat.getDateFormat(this)
                    .format(calendar.getTimeInMillis());
                AppDialog dialog = new AppDialog();

                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, 1);
                args.putLong(DELETION_DATE, calendar.getTimeInMillis());
                args.putString(AppDialog.DIALOG_MESSAGE, (getString(R.string.delete_timings_message, fromDate)));
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), null);
                break;
            default:
                throw new IllegalArgumentException("Invalid mode when receiving DatePickerDialog");
        }
    }

    private void deleteRecords(long timeInMillis) {
        Log.d(TAG, "deleteRecords: starts");

        String[] selectionArgs = new String[]{Long.toString(timeInMillis / 1000)};
        String selection = TimingsContract.Columns.TIMINGS_START_TIME + " < ?";
        Log.d(TAG, "deleteRecords: Deleting records prior to " + (timeInMillis / 1000));

        getContentResolver().delete(TimingsContract.CONTENT_URI, selection, selectionArgs);
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, this.args, this);
        Log.d(TAG, "deleteRecords: ends");
    }

    @Override
    public void onPositiveDialogResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: starts");
        deleteRecords(args.getLong(DELETION_DATE));
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, this.args, this);
    }

    @Override
    public void onNegativeDialogResult(int dialogID, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: Stub!");
    }

    @Override
    public void onDialogCancel(int dialogID) {
        Log.d(TAG, "onDialogCancel: Stub!");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_DATE, calendar.getTimeInMillis());
        outState.putBoolean(DISPLAY_WEEK, displayWeek);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: starts");

        switch (v.getId()) {
            case R.id.td_name_heading:
                args.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_NAME);
                break;
            case R.id.td_description_heading:
                args.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_DESCRIPTION);
                break;
            case R.id.td_start_heading:
                args.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_START_DATE);
                break;
            case R.id.td_duration_heading:
                args.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_DURATION);
                break;
        }

        LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, this);
    }
}