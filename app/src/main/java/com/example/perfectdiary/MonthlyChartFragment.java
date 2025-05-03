package com.example.perfectdiary;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MonthlyChartFragment extends Fragment {

    private PieChart pieChart;

    public MonthlyChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pieChart = view.findViewById(R.id.monthlyPieChart);
        setupPieChart();
        loadEmotionData();
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setCenterText("Monthly Emotions");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
    }

    private void loadEmotionData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Fake percentage-like values (summing to ~100 for realism)
        entries.add(new PieEntry(25f, "Happy"));
        entries.add(new PieEntry(15f, "Sad"));
        entries.add(new PieEntry(20f, "Angry"));
        entries.add(new PieEntry(10f, "Anxious"));
        entries.add(new PieEntry(30f, "Peaceful"));

        PieDataSet dataSet = new PieDataSet(entries, "Emotions");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }
}
