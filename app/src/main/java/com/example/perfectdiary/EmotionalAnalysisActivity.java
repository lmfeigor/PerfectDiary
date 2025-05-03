//package com.example.perfectdiary;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.github.mikephil.charting.charts.PieChart;
//import com.github.mikephil.charting.data.PieData;
//import com.github.mikephil.charting.data.PieDataSet;
//import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.utils.ColorTemplate;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Locale;
//
//public class EmotionalAnalysisActivity extends AppCompatActivity {
//
//    private PieChart emotionChart;
//    private TextView dateTextView;
//    private DatabaseHelper databaseHelper;
//    private String currentUsername;
//    private String currentTimeFrame = "Weekly"; // Default timeframe
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_emotion_analysis);
//
//        // Initialize views
//        emotionChart = findViewById(R.id.emotionChart);
//        SharedPreferences sharedPreferences = getSharedPreferences("PerfectDiaryPrefs", MODE_PRIVATE);
//        currentUsername = sharedPreferences.getString("loggedInUser", "default_user");
//        databaseHelper = new DatabaseHelper(this, currentUsername);
//        dateTextView = findViewById(R.id.dateTextView);
//
//        Button btnWeekly = findViewById(R.id.btnWeekly);
//        Button btnMonthly = findViewById(R.id.btnMonthly);
//        Button btnYearly = findViewById(R.id.btnYearly);
//
//        // Set up time frame selection
//        btnWeekly.setOnClickListener(v -> {
//            currentTimeFrame = "Weekly";
//            updateDateRange();
//            updateChart();
//        });
//
//        btnMonthly.setOnClickListener(v -> {
//            currentTimeFrame = "Monthly";
//            updateDateRange();
//            updateChart();
//        });
//
//        btnYearly.setOnClickListener(v -> {
//            currentTimeFrame = "Yearly";
//            updateDateRange();
//            updateChart();
//        });
//
//        setupChart();
//        updateDateRange(); // set default date range
//        updateChart();     // load chart
//    }
//
//    private void setupChart() {
//        emotionChart.setUsePercentValues(true);
//        emotionChart.getDescription().setEnabled(false);
//        emotionChart.setExtraOffsets(5, 10, 5, 5);
//        emotionChart.setDragDecelerationFrictionCoef(0.95f);
//        emotionChart.setDrawHoleEnabled(true);
//        emotionChart.setHoleColor(android.R.color.transparent);
//        emotionChart.setTransparentCircleRadius(61f);
//        emotionChart.animateY(1000);
//        emotionChart.setNoDataText("No data available");
//    }
//
//    private void updateDateRange() {
//        String rangeText = "";
//        switch (currentTimeFrame) {
//            case "Weekly":
//                rangeText = getCurrentWeekRange();
//                break;
//            case "Monthly":
//                rangeText = getCurrentMonthRange();
//                break;
//            case "Yearly":
//                rangeText = getCurrentYearRange();
//                break;
//        }
//        dateTextView.setText(rangeText);
//    }
//
//
//    private void updateChart() {
//        ArrayList<EmotionData> emotionData = getEmotionData(currentTimeFrame);
//
//        ArrayList<PieEntry> entries = new ArrayList<>();
//        for (EmotionData data : emotionData) {
//            entries.add(new PieEntry(data.getPercentage(), data.getEmotion()));
//        }
//
//        PieDataSet dataSet = new PieDataSet(entries, "Emotional Analysis");
//        dataSet.setSliceSpace(3f);
//        dataSet.setSelectionShift(5f);
//        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//
//        PieData pieData = new PieData(dataSet);
//        pieData.setValueTextSize(11f);
//        pieData.setValueTextColor(android.R.color.white);
//
//        emotionChart.setData(emotionData.isEmpty() ? null : pieData);
//        emotionChart.invalidate();
//    }
//
//    private ArrayList<EmotionData> getEmotionData(String timeframe) {
//        // Return an empty list – no hardcoded data
//        return new ArrayList<>();
//    }
//
//    private String getCurrentWeekRange() {
//        LocalDate today = LocalDate.now();
//        LocalDate start = today.with(DayOfWeek.MONDAY);
//        LocalDate end = today.with(DayOfWeek.SUNDAY);
//        return formatRange(start, end);
//    }
//
//    private String getCurrentMonthRange() {
//        LocalDate today = LocalDate.now();
//        LocalDate start = today.withDayOfMonth(1);
//        LocalDate end = today.withDayOfMonth(today.lengthOfMonth());
//        return formatRange(start, end);
//    }
//
//    private String getCurrentYearRange() {
//        LocalDate today = LocalDate.now();
//        LocalDate start = today.withDayOfYear(1);
//        LocalDate end = today.withDayOfYear(today.lengthOfYear());
//        return formatRange(start, end);
//    }
//
//    private String formatRange(LocalDate start, LocalDate end) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
//        return formatter.format(start) + " - " + formatter.format(end);
//    }
//}
//

package com.example.perfectdiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EmotionalAnalysisActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    //private TextView mostFrequentEmotion, leastExpressedEmotion, emotionalBalance;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotional_analysis);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        //mostFrequentEmotion = findViewById(R.id.textMostFrequent);
        //leastExpressedEmotion = findViewById(R.id.textLeastExpressed);
        //emotionalBalance = findViewById(R.id.textBalanceScore);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup chart pager adapter
        EmotionChartPagerAdapter adapter = new EmotionChartPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Weekly"); break;
                        case 1: tab.setText("Monthly"); break;
                        case 2: tab.setText("Yearly"); break;
                    }
                }).attach();

        // Sample data analysis (replace with actual logic)
        //mostFrequentEmotion.setText("Most Frequent Emotion: Happy");
        // leastExpressedEmotion.setText("Least Expressed Emotion: Angry");
        // emotionalBalance.setText("Balance Score: 78");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getOrder()) {
                    case 0: // Home
                        intent = new Intent(EmotionalAnalysisActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case 1: // Search
                        Toast.makeText(EmotionalAnalysisActivity.this, "Navigating to Search", Toast.LENGTH_SHORT).show();
                        intent = new Intent(EmotionalAnalysisActivity.this, SearchActivity.class); // Start SearchActivity
                        startActivity(intent);
                        return true;

                    case 2: // Add
                        intent = new Intent(EmotionalAnalysisActivity.this, Add.class);
                        startActivity(intent);
                        return true;
                    case 3: // Emotion
                        return true;
                    case 4: // Profile
                        intent = new Intent(EmotionalAnalysisActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

}
