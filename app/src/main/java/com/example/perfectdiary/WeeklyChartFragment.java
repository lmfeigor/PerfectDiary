package com.example.perfectdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeeklyChartFragment extends Fragment {

    private PieChart pieChart;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    public WeeklyChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.weeklyPieChart);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("PerfectDiaryPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("loggedInUser", "default_user");
        databaseHelper = new DatabaseHelper(getContext(), currentUsername);

        setupPieChart();
        loadWeeklyEmotionData();
    }

    private void setupPieChart() {
        // Basic chart configuration
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        // Center hole configuration
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setHoleRadius(30f);  // Slightly smaller hole
        pieChart.setTransparentCircleRadius(35f);

        // Center text configuration
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Weekly Emotions");
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.BLACK);

        // Interaction settings
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // Entry label styling
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setNoDataText("No emotions recorded this week");

        // Legend configuration
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(5f);
    }

    private void loadWeeklyEmotionData() {
        // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()); // Added year to the pattern
        Date now = new Date();
        String currentDate = sdf.format(now);
        String startOfWeek = getStartOfWeek(now);

        Log.d("WeeklyChart", "Loading emotions for week from " + startOfWeek + " to " + currentDate);

        List<String> emotions = databaseHelper.getEmotionsForWeek(startOfWeek, currentDate);
        Log.d("WeeklyChart", "Found " + emotions.size() + " emotions this week");

        if (emotions.isEmpty()) {
            // Handle empty chart
            pieChart.clear();
            pieChart.invalidate();
            Log.d("WeeklyChart", "No emotions found for the week");
            return;
        }

        // Count emotion occurrences
        Map<String, Integer> emotionCounts = new HashMap<>();
        for (String emotion : emotions) {
            emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
            Log.d("WeeklyChart", "Processing emotion: " + emotion + ", Count: " + emotionCounts.get(emotion));
        }

        // Create chart entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : emotionCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            Log.d("WeeklyChart", "Chart entry - " + entry.getKey() + ": " + entry.getValue());
        }

        // Configure the dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Configure the data object
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        // Set data and update the chart
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }

    private String getStartOfWeek(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()); // Added year to the pattern.
        return sdf.format(calendar.getTime());
    }

    @Override
    public void onDestroyView() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroyView();
    }
}
