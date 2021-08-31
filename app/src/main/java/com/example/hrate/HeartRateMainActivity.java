package com.example.hrate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hrate.data.AppSharedPreferences;
import com.example.hrate.data.DBConstant;
import com.example.hrate.data.HeartRateConstant;
import com.example.hrate.home.HomeFragment;
import com.example.hrate.home.TrendFragment;
import com.example.hrate.theme.MTTPCustom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import com.example.hrate.R;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Heart rate measurement # 1.3
 */
public class HeartRateMainActivity extends AppCompatActivity implements HeartRateConstant, DBConstant {

    private ViewPager viewPager;
    private TextView txtLargeTitle, txtSmallTitle;
    private MotionLayout motionLayout;
    private static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setTheme(R.style.NoActionBar);
            setContentView(R.layout.activity_heart_rate_main_appbar);
            txtLargeTitle = findViewById(R.id.txtLargeTitle);
            txtSmallTitle = findViewById(R.id.txtSmallTitle);
            Toolbar toolbar = findViewById(R.id.scrollable_toolbar);
            motionLayout = findViewById(R.id.appbar_container);
            motionLayout.setProgress(100f);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        } else {
            setContentView(R.layout.activity_heart_rate_main);
            Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        }
        setTitle(getString(R.string.heart_rate_monitor));

        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        fab = findViewById(R.id.fab_measure_hr);
        fab.hide();
        fab.setOnClickListener(v -> {
//                viewPager.setCurrentItem(0);
            Intent intent = new Intent(v.getContext(), MeasuringActivity.class);
            startActivity(intent);
        });

        viewPager.setAdapter(new TabViewPager(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) { // trend tab
                    fab.show();
                } else {// home tab
                    fab.hide();
                    motionLayout.setProgress(100f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && txtLargeTitle!=null){
            txtLargeTitle.setText(title);
            txtSmallTitle.setText(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_heart_rate_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showFabTutorial() {
        new MaterialTapTargetPrompt.Builder(HeartRateMainActivity.this)
                .setTarget(fab)
                .setPrimaryText(R.string.hr_alter_measure_btn_tutorial)
                .setPromptBackground(new MTTPCustom.DimmedCirclePromptBackground())
                .show();
        AppSharedPreferences.getInstance(this).getEditor().putBoolean(KEY_TUTORIAL_TREND_FRAG, false).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int activeTabNumber = intent.getIntExtra(EXTRA_TAB_NUMBER, 0);
        Log.d(getLocalClassName(), "active tab= " + activeTabNumber);
        viewPager.setCurrentItem(activeTabNumber);
        Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back btn
                return true;
            case R.id.menu_info:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class TabViewPager extends FragmentPagerAdapter {

        TabViewPager(FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                TrendFragment trendFragment = new TrendFragment();
                trendFragment.setOnTutorialFinishedListener(HeartRateMainActivity.this::showFabTutorial);
                return trendFragment;
            }
            return new HomeFragment();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.heart_rate_monitor);
            }
            return getString(R.string.trending);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
//            return super.getItemPosition(object);
        }
    }
}
