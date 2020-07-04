package annan.example.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static annan.example.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static annan.example.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

public class TasksContract {

    static final String TABLE_NAME = "Tasks";

    /**
     * The Uri to access the tasks table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;


    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "Name";
        public static final String TASKS_DESCRIPTION = "Description";
        public static final String TASKS_SORTORDER = "SortOrder";

        private Columns() {

        }
    }

    static Uri buildTaskUri(long taskID) {
        return ContentUris.withAppendedId(CONTENT_URI, taskID);
    }

    static long getTaskID(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
