package annan.example.tasktimer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {

    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor cursor;
    private OnTaskClickListener listener;

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Task task = new Task(cursor.getLong(cursor.getColumnIndex(TasksContract.Columns._ID)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASKS_SORTORDER)));

            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tli_edit:
                            if (listener != null) {
                                listener.onEditClick(task);
                            }
                            break;
                        case R.id.tli_delete:
                            if (listener != null) {
                                listener.onDeleteClick(task);
                            }
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            };

            View.OnLongClickListener buttonLongListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: starts");
                    if (CursorRecyclerViewAdapter.this.listener != null) {
                        CursorRecyclerViewAdapter.this.listener.onTaskLongClick(task);
                        return false;
                    }
                    return false;
                }
            };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
            holder.itemView.setOnLongClickListener(buttonLongListener);
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    /**
     * Swap in the new cursor, returning the old cursor
     * The returned old cursor is not closed
     *
     * @param newCursor The new cursor to be used
     * @return The old cursor, or null if there wasn't null
     * If the given cursor is the same instance as the new one, the method returns null
     */
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

    interface OnTaskClickListener {
        void onEditClick(@NonNull Task task);

        void onDeleteClick(@NonNull Task task);

        void onTaskLongClick(@NonNull Task task);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        ImageButton editButton;
        ImageButton deleteButton;
        View itemView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editButton = itemView.findViewById(R.id.tli_edit);
            this.deleteButton = itemView.findViewById(R.id.tli_delete);
            this.itemView = itemView;
        }
    }
}
