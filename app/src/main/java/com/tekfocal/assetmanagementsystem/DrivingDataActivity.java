package com.tekfocal.assetmanagementsystem;


import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tekfocal.assetmanagementsystem.Fragments.VehicleBadDrivingBehFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleDrivingTimeFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleFuelConsumptionFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleMileageFragement;

public class DrivingDataActivity extends AppCompatActivity  {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    String getFragmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_data);

        getFragmentName = getIntent().getStringExtra("Driving Data");

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
            //   corresponding page in the ViewPager.
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText("Total Mileage").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Fuel consumption").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Driving time").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Bad driving behaviour").setTabListener(tabListener));

        if (getFragmentName.equals("mileage")){
            ActionBar.Tab tab = actionBar.getTabAt(0);
            actionBar.selectTab(tab);
        }

        if (getFragmentName.equals("fuel")){
            ActionBar.Tab tab = actionBar.getTabAt(1);
            actionBar.selectTab(tab);
        }

        if (getFragmentName.equals("driving")){
            ActionBar.Tab tab = actionBar.getTabAt(2);
            actionBar.selectTab(tab);
        }

        if (getFragmentName.equals("bad driving")){
            ActionBar.Tab tab = actionBar.getTabAt(3);
            actionBar.selectTab(tab);
        }

        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new VehicleMileageFragement();
            if (i == 0)
                fragment = new VehicleMileageFragement();
            if (i == 1)
                fragment = new VehicleFuelConsumptionFragment();
            if (i == 2)
                fragment = new VehicleDrivingTimeFragment();
            if (i == 3)
                fragment = new VehicleBadDrivingBehFragment();

            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}