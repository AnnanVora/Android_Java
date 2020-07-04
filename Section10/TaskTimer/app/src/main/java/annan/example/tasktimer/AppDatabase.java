package annan.example.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Basic Database class for the app.
 * The Only class that should use this is {@link AppProvider}
 */

class AppDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AppDatabase";

    private static String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;
    private static AppDatabase instance = null;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;
        sSQL = "CREATE TABLE IF NOT EXISTS " + TasksContract.TABLE_NAME + "(" +
                TasksContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                TasksContract.Columns.TASKS_NAME + " TEXT NOT NULL, " +
                TasksContract.Columns.TASKS_DESCRIPTION + " TEXT, " +
                TasksContract.Columns.TASKS_SORTORDER + " INTEGER" + ");";

        Log.d(TAG, "sSQL = " + sSQL);
        db.execSQL(sSQL);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                // TODO
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown old version " + oldVersion);
        }
    }

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: Constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object.
     * @param context the content provider's context.
     * @return a SQLite database helper object.
     */
    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }

        return instance;
    }
}
