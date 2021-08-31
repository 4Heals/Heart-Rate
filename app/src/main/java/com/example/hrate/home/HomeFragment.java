package com.example.hrate.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import com.example.hrate.MeasuringActivity;
import com.example.hrate.R;
import com.example.hrate.data.AppDatabase;
import com.example.hrate.data.AppSharedPreferences;
import com.example.hrate.data.DBConstant;
import com.example.hrate.data.HeartRateResult;
import com.example.hrate.theme.MTTPCustom;
import com.example.hrate.data.HeartRateConstant;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

import com.example.hrate.animation.AppAnimation;


public class HomeFragment extends Fragment implements HeartRateConstant, DBConstant {
    private Button btnStart;
    private TextView txtHrDate, txtStatusName;
    private TextView txtHrValue, txtInProgressHrValue, txtExpectedMinValue, txtExpectedMaxValue, txtMinValue, txtMaxValue, txtAverageRange;
    private Guideline glExpMin, glExpMax, glCurrentHR;
    private int bpmValue, expectMinValue, expectMaxValue, minValue, maxValue, userHrStatus;
    private ImageView imgHrStatus;
    private Calendar hrDate;
    private String statusName;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppSharedPreferences.getInstance(getContext()).getValues().getBoolean(KEY_TUTORIAL_HOME_FRAG, true)) {
            RectanglePromptFocal rectanglePromptFocal = new RectanglePromptFocal();
            rectanglePromptFocal.setCornerRadius(30f, 30f);
            new MaterialTapTargetPrompt.Builder(requireActivity())
                    .setTarget(btnStart)
                    .setPrimaryText(R.string.hr_measure_btn_tutorial)
                    .setPromptBackground(new MTTPCustom.DimmedRectPromptBackground())
                    .setPromptFocal(rectanglePromptFocal)
                    .show();

            AppSharedPreferences.getInstance(getContext()).getEditor().putBoolean(KEY_TUTORIAL_HOME_FRAG, false).commit();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppSharedPreferences store = AppSharedPreferences.getInstance(getContext());
        AppDatabase db = AppDatabase.getInstance(getContext());
        HeartRateResult result = null;
        long lastResultId = store.getValues().getLong(KEY_LAST_RESULT_ID, -1);
        if (lastResultId != -1) {
            result = db.getResultById((int) lastResultId);
        }
        View v = null;
        if (lastResultId == -1 || result == null) {
            // no data
            v = inflater.inflate(R.layout.fragment_heart_rate_home, container, false);
            btnStart = v.findViewById(R.id.btn_measure_hr_first_start);
        } else {
            v = inflater.inflate(R.layout.fragment_heart_rate_home2, container, false);
            btnStart = v.findViewById(R.id.btn_measure_hr);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MeasuringActivity.class);
                startActivity(intent);
            }
        });

        if (lastResultId == -1) {
            return v;
        }

        imgHrStatus = (ImageView) v.findViewById(R.id.img_hr_status_home_frag);

        txtHrDate = (TextView) v.findViewById(R.id.tv_hr_date2);
        txtStatusName = (TextView) v.findViewById(R.id.tv_hr_status_name2);
        txtHrValue = (TextView) v.findViewById(R.id.tv_heart_rate_value2);
        txtInProgressHrValue = (TextView) v.findViewById(R.id.tv_current_hr2);
        txtExpectedMinValue = (TextView) v.findViewById(R.id.tv_expect_hr_min2);
        txtExpectedMaxValue = (TextView) v.findViewById(R.id.tv_expect_hr_max2);
        txtMinValue = (TextView) v.findViewById(R.id.tv_hr_min2);
        txtMaxValue = (TextView) v.findViewById(R.id.tv_hr_max2);
        txtAverageRange = (TextView) v.findViewById(R.id.tv_hr_range_description2);

        glExpMin = (Guideline) v.findViewById(R.id.guideline_expect_min_hr2);
        glExpMax = (Guideline) v.findViewById(R.id.guideline_expect_max_hr2);
        glCurrentHR = (Guideline) v.findViewById(R.id.guideline_current_hr2);

        assert result != null;
        bpmValue = result.getValue();
        hrDate = Calendar.getInstance();
        hrDate.setTimeInMillis(result.getTimestamp() * 1000);
        setStatus(result.getUserStatusId());
        return v;
    }

    private void setStatus(int statusCode) {
        minValue = userHRMinValue[statusCode];
        maxValue = userHRMaxValue[statusCode];
        expectMinValue = userHRExpectedMinValue[statusCode];
        expectMaxValue = userHRExpectedMaxValue[statusCode];
        userHrStatus = statusCode;
        statusName = getString(userHRStatusText[statusCode]);
        statusName = statusName.substring(0, 1).toUpperCase() + statusName.substring(1);
        setHeartRateViewItem();
    }

    @SuppressLint("SetTextI18n")
    private void setHeartRateViewItem() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd MMM", java.util.Locale.getDefault());
        txtHrDate.setText(simpleDateFormat.format(hrDate.getTime()));

        imgHrStatus.setImageResource(userHRStatusImgId[userHrStatus]);
        txtStatusName.setText(statusName);

        txtHrValue.setText("" + bpmValue);
        txtMinValue.setText("" + minValue);
        txtMaxValue.setText("" + maxValue);
        txtExpectedMinValue.setText("" + expectMinValue);
        txtExpectedMaxValue.setText("" + expectMaxValue);
        txtAverageRange.setText(String.format(getString(R.string.hr_avg_range), getString(userHRStatusText[userHrStatus])));
        if (Math.abs(bpmValue - expectMinValue) > 3 && Math.abs(bpmValue - expectMaxValue) > 3 && Math.abs(bpmValue - minValue) > 3 && Math.abs(bpmValue - maxValue) > 3) {
            txtInProgressHrValue.setText("" + bpmValue);
        } else {
            txtInProgressHrValue.setText("");
        }
        if (bpmValue <= minValue) {
            AppAnimation.changeGuidelineWithAnim(glCurrentHR, 0.02f);
            txtMinValue.setText("");
        } else if (bpmValue >= maxValue) {
            AppAnimation.changeGuidelineWithAnim(glCurrentHR, 0.98f);
            txtMaxValue.setText("");
        } else {
            AppAnimation.changeGuidelineWithAnim(glCurrentHR, 0.04f + (float) (bpmValue - minValue) / (float) (maxValue - minValue) * 0.92f);
        }
        AppAnimation.changeGuidelineWithAnim(glExpMin, 0.04f + (float) (expectMinValue - minValue) / (float) (maxValue - minValue) * 0.92f);
        AppAnimation.changeGuidelineWithAnim(glExpMax, 0.04f + (float) (expectMaxValue - minValue) / (float) (maxValue - minValue) * 0.92f);
    }
}
