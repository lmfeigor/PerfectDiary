package com.example.perfectdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryAdapter.DiaryViewHolder> {
    private Context context;
    private List<DiaryEntry> diaryEntries;

    public DiaryEntryAdapter(Context context, List<DiaryEntry> diaryEntries) {
        this.context = context;
        this.diaryEntries = diaryEntries;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary_entry, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry currentEntry = diaryEntries.get(position);

        // 设置标题和日期
        holder.titleTextView.setText(currentEntry.getTitle());
        holder.dateTextView.setText(currentEntry.getDateTime());

        // 确保背景颜色正确设置
        int backgroundColor = currentEntry.getBackgroundColor();
        if (backgroundColor != 0) {
            // 如果有具体的颜色值，使用该颜色
            holder.entryCardView.setBackgroundColor(ContextCompat.getColor(context, backgroundColor));
        } else {
            // 如果没有颜色值，使用默认的浅灰色
            holder.entryCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
        }
    }

    @Override
    public int getItemCount() {
        return diaryEntries.size();
    }

    // 方法：更新搜索结果时的条目
    public void setDiaryEntries(List<DiaryEntry> newEntries) {
        diaryEntries.clear();
        diaryEntries.addAll(newEntries);
        notifyDataSetChanged();
    }

    // ViewHolder 类，用于性能优化
    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        View entryCardView;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.entryTitleTextView);
            dateTextView = itemView.findViewById(R.id.entryDateTextView);
            entryCardView = itemView.findViewById(R.id.entryCardView);
        }
    }
}