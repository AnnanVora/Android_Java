package annan.example.flickerbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus {
    IDLE,
    PROCESSING,
    NOT_INITIALIZED,
    FAILED_OR_EMPTY,
    OK
}

class GetRawData extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetRawData";
    private DownloadStatus downloadStatus;
    private final OnDownloadComplete callback;

    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callback) {
        downloadStatus = DownloadStatus.IDLE;
        this.callback = callback;
    }

    void runInSameThread(String s) {
        Log.d(TAG, "runInSameThread: starts");
        if (callback != null) {
            callback.onDownloadComplete(doInBackground(s), downloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }

    @Override
    protected void onPostExecute(String s) {
        if (callback != null) {
            callback.onDownloadComplete(s, downloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        if (strings == null) {
            downloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }
        
        try {
            downloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while (null != (line = reader.readLine())) {
                result.append(line).append("\n");
            }

            downloadStatus = DownloadStatus.OK;
            return result.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URl" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data" + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception, needs permission" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing reader Stream" + e.getMessage());
                }
            }
        }

        downloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
