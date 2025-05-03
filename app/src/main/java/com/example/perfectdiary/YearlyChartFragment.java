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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YearlyChartFragment extends Fragment {

    private PieChart pieChart;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    public YearlyChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yearly_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize chart and database helper
        pieChart = view.findViewById(R.id.yearlyPieChart);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("PerfectDiaryPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("loggedInUser", "default_user");
        databaseHelper = new DatabaseHelper(getContext(), currentUsername);

        setupPieChart();
        loadYearlyEmotionData();
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
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(35f);

        // Center text configuration
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Yearly Emotions");
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.BLACK);

        // Interaction settings
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // Entry label styling
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setNoDataText("No emotions recorded this year");

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

    private void loadYearlyEmotionData() {
        // Get current year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Log.d("YearlyChart", "Loading emotions for year: " + currentYear);

        // Get emotions for the current year
        List<String> emotions = databaseHelper.getEmotionsForYear(currentYear);
        Log.d("YearlyChart", "Found " + emotions.size() + " emotions");

        if (emotions.isEmpty()) {
            // Handle empty state
            pieChart.clear();
            pieChart.invalidate();
            Log.d("YearlyChart", "No emotions found for the year");
            return;
        }

        // Count emotion occurrences
        Map<String, Integer> emotionCounts = new HashMap<>();
        for (String emotion : emotions) {
            emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
            Log.d("YearlyChart", "Processing emotion: " + emotion);
        }

        // Create chart entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : emotionCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            Log.d("YearlyChart", "Chart entry - " + entry.getKey() + ": " + entry.getValue());
        }

        // Configure data set
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Configure data
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        // Set data and refresh
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }

    @Override
    public void onDestroyView() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroyView();
    }
}