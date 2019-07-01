package com.tekfocal.assetmanagementsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.tekfocal.assetmanagementsystem.Fragments.DrivingDataFragment;
import com.tekfocal.assetmanagementsystem.Fragments.HelpFragment;
import com.tekfocal.assetmanagementsystem.Fragments.MapsFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleAlertsFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleListFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleMapFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleParameterFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleSettingsFragment;
import com.tekfocal.assetmanagementsystem.Models.Vehicle;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    ImageButton maps_btn, vehicle_list_btn, car_parametr_btn, config_btn, driving_data_btn, setting_btn, help_btn;
    RequestQueue queue;
    String selectedTruck = "";
    boolean maps_fragment_check;
    Toolbar toolbar;
    TextView titleTag;

    Boolean isMapBtnClick = true, isListBtnClick = true, isParamtrBtnClick = true, isAertBtnClick = true, isDrivingBtnClick = true
    , isSsettingbtnClick = true, isHelpbtnClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
            VehicleMapFragment mapsFrag = new VehicleMapFragment();
            loadFragment(mapsFrag);
            maps_fragment_check = true;
        }

        init();
    }

    public void init(){
        titleTag = findViewById(R.id.title_activity);

        maps_btn = findViewById(R.id.map_btn);
        vehicle_list_btn = findViewById(R.id.car_btn);
        car_parametr_btn = findViewById(R.id.parammeter_btn);
        config_btn = findViewById(R.id.alert_config_btn);
        driving_data_btn = findViewById(R.id.driving_data);
        setting_btn = findViewById(R.id.setting_btn);
        help_btn = findViewById(R.id.help_btn);

        maps_btn.setOnClickListener(this);
        vehicle_list_btn.setOnClickListener(this);
        car_parametr_btn.setOnClickListener(this);
        config_btn.setOnClickListener(this);
        driving_data_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);
        help_btn.setOnClickListener(this);

        if (maps_fragment_check)
            maps_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        queue = Volley.newRequestQueue(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        if (!titleTag.getText().toString().equals("Maps")) {

            VehicleMapFragment mapsFrag = new VehicleMapFragment();
            loadFragment(mapsFrag);
            maps_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

            titleTag.setText("Maps");
        } else {
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            else {
                finish();
            }
            return true;
        }
        if (id == R.id.action_asset){
            if (!isOnline()){
                Toast.makeText(Main2Activity.this,"No Internet Connection!", Toast.LENGTH_SHORT).show();
            }else {
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
                finish();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ResourceType")
    public void loadFragment(Fragment fragment){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.addToBackStack(null);

        ft.replace(R.id.frame, fragment,"Fragment");
        ft.commitAllowingStateLoss();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.map_btn:
                maps_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isMapBtnClick){
                    VehicleMapFragment mapsFrag = new VehicleMapFragment();
                    loadFragment(mapsFrag);
                    titleTag.setText("Maps");

                    isMapBtnClick = false;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isHelpbtnClick = true;
                }

                break;
            case R.id.car_btn:
                vehicle_list_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isListBtnClick) {
                    VehicleListFragment vehicleFrag = new VehicleListFragment();
                    loadFragment(vehicleFrag);
                    titleTag.setText("Vehicles List");

                    isMapBtnClick = true;
                    isListBtnClick = false;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isHelpbtnClick = true;
                }

                break;
            case R.id.alert_config_btn:
                config_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isAertBtnClick) {
                    VehicleAlertsFragment alertsFragment = new VehicleAlertsFragment();
                    loadFragment(alertsFragment);
                    titleTag.setText("Alerts");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = false;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isHelpbtnClick = true;
                }

                break;
            case R.id.parammeter_btn:
                car_parametr_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isParamtrBtnClick) {
                    VehicleParameterFragment truckParameterFragment = new VehicleParameterFragment();
                    loadFragment(truckParameterFragment);
                    titleTag.setText("Vehicle Parameter");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = false;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isHelpbtnClick = true;

                }

                break;

            case R.id.driving_data:
                driving_data_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isDrivingBtnClick) {
                    DrivingDataFragment drivingDataFragment = new DrivingDataFragment();
                    loadFragment(drivingDataFragment);
                    titleTag.setText("Driving Data");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = false;
                    isSsettingbtnClick = true;
                    isHelpbtnClick = true;
                }

                break;

            case R.id.setting_btn:
                setting_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isSsettingbtnClick) {
                    VehicleSettingsFragment settingsFragment = new VehicleSettingsFragment();
                    loadFragment(settingsFragment);
                    titleTag.setText("Settings");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = false;
                    isHelpbtnClick = true;
                }

                break;

                case R.id.help_btn:
                help_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isHelpbtnClick) {
                    HelpFragment helpFragment = new HelpFragment();
                    loadFragment(helpFragment);
                    titleTag.setText("Help");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isHelpbtnClick = false;
                }
                break;
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
