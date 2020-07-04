package annan.example.tasktimer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean twoPane = false;
    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                taskEditRequest(null);
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
    
    private void taskEditRequest(@Nullable Task task) {
        if (twoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");
        } else {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) {
                detailIntent.putExtra(Task.class.getSimpleName(), task);
            }
            startActivity(detailIntent);
        }
    }
}