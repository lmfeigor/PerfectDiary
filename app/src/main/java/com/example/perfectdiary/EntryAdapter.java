package com.example.perfectdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private List<DiaryEntry> entryList;

    public EntryAdapter(List<DiaryEntry> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single diary entry item
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary_entry, parent, false); // You need to create this layout file
        return new EntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DiaryEntry currentEntry = entryList.get(position);
        holder.dateTextView.setText(formatDate(currentEntry.getDate()));
        holder.timeTextView.setText(formatTime(currentEntry.getDate()));
        // You'll need to find a way to represent the "three horizontal lines" visually based on your entry data
        // This might involve setting the visibility or content of other views in your item_diary_entry.xml
        // For now, let's just leave a placeholder comment.
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView timeTextView;
        // You might need more TextViews or other Views to represent your entry

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.entryDateTextView); // Assuming these IDs exist in item_diary_entry.xml
            timeTextView = itemView.findViewById(R.id.entryTimeTextView);
            // Initialize other views here
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM ''yy EEE", Locale.getDefault());
        return sdf.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}