package annan.example.flickrbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {
    
    private static final String TAG = "MainActivity";
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(new ArrayList<Photo>(), this);
        recyclerView.setAdapter(flickrRecyclerViewAdapter);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume starts");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this, "https://api.flickr.com/services/feeds/photos_public.gne", "en-us", true);
        getFlickrJsonData.execute("flower, yellow");
        Log.d(TAG, "onResume ends");
    }

      @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        Log.d(TAG, "onOptionsItemSelected() returned: super");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status) {

        Log.d(TAG, "onDataAvailable: starts");
        if (status == DownloadStatus.OK) {
            flickrRecyclerViewAdapter.loadNewData(data);
        } else {
            Log.e(TAG, "onDownloadComplete: failed with status" + status);
        }
        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(MainActivity.this, "Normal tap at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts");
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER, flickrRecyclerViewAdapter.getPhoto(position));
	startActivity(intent);
    }
}
