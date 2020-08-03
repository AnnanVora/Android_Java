package annan.example.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provider for the Task Timer app. This is the only class that knows about {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {

    static final String CONTENT_AUTHORITY = "annan.example.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String TAG = "AppProvider";
    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;
    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;
    //    private static final int TASK_TIMING         = 300;
//    private static final int TASK_TIMINGS_ID     = 301;
    private static final int TASKS_DURATIONS = 400;
    private static final int TASKS_DURATIONS_ID = 401;
    public static final UriMatcher uriMatcher = buildUriMatcher();
    private AppDatabase openHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);
        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASKS_DURATIONS);
        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASKS_DURATIONS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = uriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;
            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskID = TasksContract.getTaskID(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskID);
                break;
            case TIMINGS:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                break;
            case TIMINGS_ID:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                long timingID = TimingsContract.getTimingID(uri);
                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingID);
                break;
            case TASKS_DURATIONS:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                break;
            case TASKS_DURATIONS_ID:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                long durationID = DurationsContract.getDurationId(uri);
                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationID);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;
            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;
            case TIMINGS:
                return TimingsContract.CONTENT_TYPE;
            case TIMINGS_ID:
                return TimingsContract.CONTENT_ITEM_TYPE;
            case TASKS_DURATIONS:
                return DurationsContract.CONTENT_TYPE;
            case TASKS_DURATIONS_ID:
                return DurationsContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown Uri: " + uri);
        }
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Entering insert called with uri " + uri);
        final int match = uriMatcher.match(uri);
        Log.d(TAG, "insert: match is " + match);

        final SQLiteDatabase db;
        Uri returnUri;
        long recordID;

        switch (match) {
            case TASKS:
                db = openHelper.getWritableDatabase();
                recordID = db.insert(TasksContract.TABLE_NAME, null, values);
                if (recordID >= 0) {
                    returnUri = TasksContract.buildTaskUri(recordID);
                } else {
                    throw new SQLException("Failed to insert into " + uri.toString());
                }
                break;
            case TIMINGS:
                db = openHelper.getWritableDatabase();
                recordID = db.insert(TimingsContract.TABLE_NAME, null, values);
                if (recordID >= 0) {
                    returnUri = TimingsContract.buildTimingUri(recordID);
                } else {
                    throw new SQLException("Failed to insert into " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (recordID >= 0) {
            Log.d(TAG, "insert: setting notified change with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "insert: nothing inserted");
            System.out.println("Hello");
        }
        Log.d(TAG, "insert: Exiting, returning " + returnUri);
        return returnUri;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete: called with uri " + uri);
        final int match = uriMatcher.match(uri);
        Log.d(TAG, "delete: match = " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = openHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;
            case TASKS_ID:
                db = openHelper.getWritableDatabase();
                long taskID = TasksContract.getTaskID(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskID;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ");";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case TIMINGS:
                db = openHelper.getWritableDatabase();
                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
                break;
            case TIMINGS_ID:
                db = openHelper.getWritableDatabase();
                long timingID = TimingsContract.getTimingID(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingID;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ");";
                }
                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (count > 0) {
            Log.d(TAG, "delete: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: nothing deleted");
        }
        Log.d(TAG, "delete: exiting, returning " + count);
        return count;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = uriMatcher.match(uri);
        Log.d(TAG, "update: match = " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = openHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASKS_ID:
                db = openHelper.getWritableDatabase();
                long taskID = TasksContract.getTaskID(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskID;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ");";
                }
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            case TIMINGS:
                db = openHelper.getWritableDatabase();
                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TIMINGS_ID:
                db = openHelper.getWritableDatabase();
                long timingID = TimingsContract.getTimingID(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingID;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ");";
                }
                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (count > 0) {
            Log.d(TAG, "update" + ": Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing updated");
        }

        Log.d(TAG, "update: exiting, returning " + count);
        return count;
    }
}
