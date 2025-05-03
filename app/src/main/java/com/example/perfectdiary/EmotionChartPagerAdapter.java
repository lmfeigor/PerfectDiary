package com.example.perfectdiary;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.perfectdiary.WeeklyChartFragment;


public class EmotionChartPagerAdapter extends FragmentStateAdapter {

    public EmotionChartPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WeeklyChartFragment(); // Make sure the fragment class is imported
            case 1:
                return new MonthlyChartFragment();
            case 2:
                return new YearlyChartFragment();
            default:
                return new WeeklyChartFragment(); // Default fragment
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Weekly, Monthly, Yearly
    }
}
