package annan.example.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provider for the Task Timer app. This is the only class that knows about {@link AppDatabase}
 */
public class AppProvider extends ContentProvider {

    private static final String TAG = "AppProvider";

    private AppDatabase openHelper;

    public static final UriMatcher uriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "annan.example.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS               = 100;
    private static final int TASKS_ID            = 101;
    private static final int TIMING              = 200;
    private static final int TIMINGS_ID          = 201;
//    private static final int TASK_TIMING         = 300;
//    private static final int TASK_TIMINGS_ID     = 301;
    private static final int TASKS_DURATIONS     = 400;
    private static final int TASKS_DURATIONS_ID  = 401;


    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
