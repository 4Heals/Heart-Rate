package com.example.hrate.data;

import com.example.hrate.R;

public interface HeartRateConstant {
    String EXTRA_TAB_NUMBER = "extra_tab_number";
    String EXTRA_HEART_RATE_VALUE = "extra_hr_value";

    // fixed User Heart Rate Status Data
    int USER_STATUS_REST = 0;
    int USER_STATUS_RUN = 1;
    int USER_STATUS_TIRED = 2;
    int USER_STATUS_EXCITED = 3;
    int[] userHRStatusImgId = {R.drawable.rested, R.drawable.run, R.drawable.tired, R.drawable.excited};
    int[] userHRStatusText = {R.string.hr_resting, R.string.hr_after_exercise, R.string.hr_tired, R.string.hr_excited};
    int[] userHRExpectedMaxValue = {76, 120, 76, 100};
    int[] userHRExpectedMinValue = {61, 80, 61, 70};
    int[] userHRMaxValue = {120, 150, 120, 120};
    int[] userHRMinValue = {40, 40, 40, 40};
}
