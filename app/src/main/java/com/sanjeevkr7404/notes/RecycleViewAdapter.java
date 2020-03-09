package com.sanjeevkr7404.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sanjeevkr7404.notes.database.Note;

public class RecycleViewAdapter extends ListAdapter<Note, RecycleViewAdapter.MyViewHolder> {

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }
    };
    OnClickListener listener;

    RecycleViewAdapter() {
        super(DIFF_CALLBACK);
    }

    Note getNote(int position) {
        return getCurrentList().get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout;
        layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.note, parent, false);
        return new MyViewHolder(layout, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = getItem(position).getTitle();
        if (title.isEmpty())
            title = getItem(position).getDescription();
        holder.setTitle(title);
    }

    public interface OnClickListener {
        void OnClick(int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(final View itemView, final OnClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.OnClick(pos);
                        }
                    }
                }
            });
        }

        void setTitle(String title) {
            TextView mTitle = itemView.findViewById(R.id.note_title);
            mTitle.setText(title);
        }
    }
}
