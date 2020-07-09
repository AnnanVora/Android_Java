package annan.example.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static annan.example.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static annan.example.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

public class TimingsContract {

    static final String TABLE_NAME = "Timings";

    /**
     * The Uri to access the timings table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTimingUri(long timingID) {
        return ContentUris.withAppendedId(CONTENT_URI, timingID);
    }

    public static long getTimingID(Uri uri) {
        return ContentUris.parseId(uri);
    }

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TIMINGS_TASK_ID = "TaskID";
        public static final String TIMINGS_START_TIME = "StartTime";
        public static final String TIMINGS_DURATION = "Duration";

        private Columns() {

        }
    }
}
