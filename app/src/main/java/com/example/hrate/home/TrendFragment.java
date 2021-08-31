package com.example.hrate.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrate.algorithm.DateUtils;
import com.example.hrate.data.AppDatabase;
import com.example.hrate.data.DBConstant;
import com.example.hrate.data.HeartRateResult;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import com.example.hrate.R;

import com.example.hrate.data.AppSharedPreferences;
import com.example.hrate.theme.MTTPCustom;
import com.example.hrate.data.HeartRateConstant;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class TrendFragment extends Fragment implements OnChartValueSelectedListener, HeartRateConstant, DBConstant {

    private BarChart chart;
    private TextView txtAvgValue, txtMaxValue, txtMinValue, txtChartTopTitle;
    private OnTutorialFinished callback;
    private HRHistoryAdapter rvHrAdapter;
    private int weekDay = 1;
    private long weekOffset = 0;
    private AppDatabase db = null;

    // demo data
    private ArrayList<Integer> maxValues = new ArrayList<>(); //int[] maxValues = {120, 111, 0, 100, 99, 98, 103}
    private ArrayList<Integer> avgValues = new ArrayList<>(); //int[] avgValues = {80, 86, 0, 99, 70, 77, 82};
    private ArrayList<Integer> minValues = new ArrayList<>(); //int[] minValues = {60, 61, 0, 63, 64, 65, 66};

    public interface OnTutorialFinished {
        void onFinished();
    }

    public TrendFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        if (!AppSharedPreferences.getInstance(getContext()).getValues().getBoolean(KEY_TUTORIAL_TREND_FRAG, true)) {
            return;
        }
        new MaterialTapTargetPrompt.Builder(requireActivity())
                .setTarget(chart)
                .setPrimaryText(R.string.hr_chart_tutorial)
                .setPromptBackground(new MTTPCustom.DimmedRectPromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHED) {
                        callback.onFinished();
                    }
                })
                .show();
    }

    public void setOnTutorialFinishedListener(OnTutorialFinished callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        weekDay = DateUtils.getCurrentWeekDay();
        db = AppDatabase.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_heart_rate_trend, container, false);

        txtChartTopTitle = view.findViewById(R.id.tv_select_a_day_to_view_results);
        chart = view.findViewById(R.id.chart_weekly_hr);

        Button btnDateBack = view.findViewById(R.id.btn_date_back);
        Button btnDateNext = view.findViewById(R.id.btn_date_next);

        txtAvgValue = view.findViewById(R.id.tv_stat_avg_value);
        txtMinValue = view.findViewById(R.id.tv_stat_min_value);
        txtMaxValue = view.findViewById(R.id.tv_stat_max_value);

        RecyclerView rvHrHistory = view.findViewById(R.id.rv_hr_history);
        rvHrAdapter = new HRHistoryAdapter(new ArrayList<>());
        rvHrHistory.setAdapter(rvHrAdapter);
        rvHrHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        chart.setOnChartValueSelectedListener(this);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        chart.setMaxVisibleValueCount(60);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new DateFormatSymbols().getShortWeekdays()[(int) (value + 6) % 7 + 1];
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        setData();
        updateAvgMaxMin(0);

        btnDateBack.setOnClickListener(v -> {
            weekOffset += 1;
            String startWeekDay = DateUtils.getDateFromTimestamp(System.currentTimeMillis() - (weekOffset + 1) * DateUtils.WEEK_MILIS + DateUtils.DAY_MILIS);
            String endWeekDay = DateUtils.getDateFromTimestamp(System.currentTimeMillis() - weekOffset * DateUtils.WEEK_MILIS);
            String weekAgoString = getString(R.string.hr_n_week_ago);
            txtChartTopTitle.setText(String.format(getString(R.string.hr_week_from_date_to_date), String.format(weekAgoString, weekOffset), startWeekDay, endWeekDay));
            setData();
        });
        btnDateNext.setOnClickListener(v -> {
            weekOffset -= 1;
            String weekAgoString = getString(R.string.hr_n_week_ago);
            if (weekOffset <= 0) {
                weekOffset = 0;
                weekAgoString = getString(R.string.hr_this_week);
            }
            String startWeekDay = DateUtils.getDateFromTimestamp(System.currentTimeMillis() - (weekOffset + 1) * DateUtils.WEEK_MILIS + DateUtils.DAY_MILIS);
            String endWeekDay = DateUtils.getDateFromTimestamp(System.currentTimeMillis() - weekOffset * DateUtils.WEEK_MILIS);

            txtChartTopTitle.setText(String.format(getString(R.string.hr_week_from_date_to_date), String.format(weekAgoString, weekOffset), startWeekDay, endWeekDay));
            setData();
        });
        return view;
    }


    @SuppressLint("SetTextI18n")
    private void updateAvgMaxMin(int minusDay) {

        // X = weekDay - minusDay
        if (minusDay < 0 || minusDay >= maxValues.size()) {
            return;
        }
        txtMaxValue.setText("" + maxValues.get(minusDay));
        txtMinValue.setText("" + minValues.get(minusDay));
        txtAvgValue.setText("" + avgValues.get(minusDay));
        long startTime = DateUtils.getDayStartTimestamp(minusDay) - weekOffset * 7 * 24 * 60 * 60;
        long endTime = startTime + 24 * 60 * 60;
        ArrayList<HeartRateResult> results = db.getResultInTimeRange(startTime, endTime);
        rvHrAdapter.updateDataSet(results);
    }

    private void setData() {

        ArrayList<BarEntry> values = new ArrayList<>();
        AppDatabase db = AppDatabase.getInstance(getContext());

        for (int minusDay = (int) 0; minusDay < 7; minusDay++) {
            long startTime = DateUtils.getDayStartTimestamp(minusDay) - (weekOffset * 7 * 24 * 60 * 60);
            long endTime = startTime + (24 * 60 * 60);
            ArrayList<HeartRateResult> results;
            results = db.getResultInTimeRange(startTime, endTime);
            int sum = 0, max = -1, min = 2000;
            if (results.size() == 0) {
                max = 0;
                min = 0;
                values.add(new BarEntry(weekDay - minusDay, 0));
                continue;
            }
            for (HeartRateResult result : results) {
                int value = result.getValue();
                sum += value;
                max = Math.max(max, value);
                min = Math.min(min, value);
            }
            int avg = sum / results.size();
            avgValues.add(avg);
            minValues.add(min);
            maxValues.add(max);
            // X = weekDay - minusDay, Y = avg
            values.add(new BarEntry(weekDay - minusDay, avg));
        }
        Collections.reverse(values);
        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();
        } else {
            set1 = new BarDataSet(values, "Last week heart rate chart");
            set1.setDrawIcons(false);
            set1.setColor(getResources().getColor(R.color.hrRedChart));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        // X = weekDay - minusDay => minusDay = weekDay - X
        updateAvgMaxMin(weekDay - (int) e.getX());
    }

    @Override
    public void onNothingSelected() {
    }

    /**
     * History Recycler View: ViewHolder
     */
    static class HRHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView txtHRTime, txtHrValue, txtHrTitle;
        ImageView imgHRStatus;

        HRHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHRStatus = itemView.findViewById(R.id.img_hr_history_status);
            txtHRTime = itemView.findViewById(R.id.tv_history_hr_timestamp);
            txtHrValue = itemView.findViewById(R.id.tv_history_hr_value);
            txtHrTitle = itemView.findViewById(R.id.tv_history_hr_title);
        }
    }

    /**
     * History Recycler View: Adapter
     */
    public class HRHistoryAdapter extends RecyclerView.Adapter<HRHistoryViewHolder> {
        ArrayList<HeartRateResult> results;

        HRHistoryAdapter(ArrayList<HeartRateResult> results) {
            this.results = results;
        }

        @NonNull
        @Override
        public HRHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_hr_history, parent, false);
            return new HRHistoryViewHolder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull HRHistoryViewHolder holder, int position) {
            HeartRateResult result = results.get(position);
            String usrStatusTxt = getString(userHRStatusText[result.getUserStatusId()]);
            usrStatusTxt = usrStatusTxt.substring(0, 1).toUpperCase() + usrStatusTxt.substring(1);
            holder.txtHrValue.setText(String.format("%d bpm", result.getValue()));
            holder.imgHRStatus.setImageDrawable(getResources().getDrawable(userHRStatusImgId[result.getUserStatusId()]));
            if (result.getNote().length() > 0) {
                holder.txtHrTitle.setText(String.format("%s (%s)", result.getNote(), usrStatusTxt));
            } else {
                holder.txtHrTitle.setText(usrStatusTxt);
            }
            holder.txtHRTime.setText(result.getTime());
        }

        void updateDataSet(ArrayList<HeartRateResult> results) {
            this.results = results;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }
}
