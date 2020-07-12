package annan.example.tasktimer;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class DurationsRVAdapter extends RecyclerView.Adapter<DurationsRVAdapter.ViewHolder> {

    private Cursor cursor;
    private final java.text.DateFormat dateFormat;

    public DurationsRVAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.dateFormat = DateFormat.getDateFormat(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (this.cursor != null && cursor.getCount() != 0) {
            if (!this.cursor .moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }
            String name = this.cursor.getString(this.cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_NAME));
            String description = this.cursor.getString(this.cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
            long startTime = this.cursor.getLong(this.cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
            long totalDuration = this.cursor.getLong(this.cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));
        
            holder.name.setText(name);
            if (holder.description != null) {
                holder.description.setText(description);
            }

            String userDate = this.dateFormat.format(startTime * 1000);
            holder.startDate.setText(userDate);
            holder.duration.setText(formatDuration(totalDuration));
        }
    }

    private String formatDuration(long durationInSec) {
        long hrs = durationInSec / 3600;
        long remainder = durationInSec - (hrs * 3600);
        long min = remainder / 60;
        long secs = remainder - (min / 60);

        return String.format(Locale.US, "%02d:%02d:%02d", hrs, min, secs);
    }

    @Override
    public int getItemCount() {
        if (cursor == null || cursor.getCount() == 0) {
            return 1;
        } else {
            return cursor.getCount();
        }
    }

    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }

        int numItems = getItemCount();

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, numItems);
        }
        return oldCursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView startDate;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.td_name);
            this.description = itemView.findViewById(R.id.td_description);
            this.startDate = itemView.findViewById(R.id.td_start);
            this.duration = itemView.findViewById(R.id.td_duration);
        }
    }
}
