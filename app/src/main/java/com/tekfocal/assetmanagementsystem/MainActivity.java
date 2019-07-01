package com.tekfocal.assetmanagementsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.Fragments.AlertsFragment;
import com.tekfocal.assetmanagementsystem.Fragments.DrivingDataFragment;
import com.tekfocal.assetmanagementsystem.Fragments.HelpFragment;
import com.tekfocal.assetmanagementsystem.Fragments.MapsFragment;
import com.tekfocal.assetmanagementsystem.Fragments.PackagesFragment;
import com.tekfocal.assetmanagementsystem.Fragments.SensorsFragment;
import com.tekfocal.assetmanagementsystem.Fragments.SettingsFragment;
import com.tekfocal.assetmanagementsystem.Fragments.TruckParameterFragment;
import com.tekfocal.assetmanagementsystem.Fragments.VehicleListFragment;
import com.tekfocal.assetmanagementsystem.Models.Package;
import com.tekfocal.assetmanagementsystem.Volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton maps_btn, vehicle_list_btn, car_parametr_btn, config_btn, driving_data_btn, setting_btn, packages_btn, sensors_btn, help_btn;
    RequestQueue queue;
    String selectedTruck = "";
    boolean maps_fragment_check;
    Toolbar toolbar;
    TextView titleTag;

    Boolean isMapBtnClick = true, isListBtnClick = true, isParamtrBtnClick = true, isAertBtnClick = true, isDrivingBtnClick = true, isSsettingbtnClick = true,
            isPackageBtnClick = true, isSensorBtnClick = true, isHelpbtnClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
            MapsFragment mapsFrag = new MapsFragment();
            loadFragment(mapsFrag);
            maps_fragment_check = true;
        }

        init();
    }

    public void init(){
        titleTag = findViewById(R.id.title_activity);

        maps_btn = findViewById(R.id.map_btn);
        vehicle_list_btn = findViewById(R.id.truck_btn);
        car_parametr_btn = findViewById(R.id.parammeter_btn);
        config_btn = findViewById(R.id.alert_config_btn);
        driving_data_btn = findViewById(R.id.driving_data);
        setting_btn = findViewById(R.id.setting_btn);
        packages_btn = findViewById(R.id.packages_btn);
        sensors_btn = findViewById(R.id.sensor_btn);
        help_btn = findViewById(R.id.help_btn);

        maps_btn.setOnClickListener(this);
        vehicle_list_btn.setOnClickListener(this);
        car_parametr_btn.setOnClickListener(this);
        config_btn.setOnClickListener(this);
        driving_data_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);
        packages_btn.setOnClickListener(this);
        sensors_btn.setOnClickListener(this);
        help_btn.setOnClickListener(this);

        if (maps_fragment_check)
            maps_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);

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
            else
                finish();
            return true;
        }
            if (id == R.id.action_smart){
                if (!isOnline()){
                    Toast.makeText(MainActivity.this,"No Internet Connection!", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    finish();
                }
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceType")
    public void loadFragment(Fragment fragment){

        Bundle bundle = new Bundle();
        bundle.putString("selectedtruck",selectedTruck);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.addToBackStack(null);

        fragment.setArguments(bundle);
        ft.replace(R.id.frame, fragment,"Frament");
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
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isMapBtnClick) {
                    MapsFragment mapsFrag = new MapsFragment();
                    loadFragment(mapsFrag);
                    titleTag.setText("Maps");

                    isMapBtnClick = false;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
                    isHelpbtnClick = true;
                }

                break;
            case R.id.truck_btn:
                vehicle_list_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isListBtnClick) {
                    VehicleListFragment vehicleFrag = new VehicleListFragment();
                    loadFragment(vehicleFrag);
                    titleTag.setText("Truck List");

                    isMapBtnClick = true;
                    isListBtnClick = false;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
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
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isAertBtnClick) {
                    AlertsFragment alertsFragment = new AlertsFragment();
                    loadFragment(alertsFragment);
                    titleTag.setText("Alerts");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = false;
                    isDrivingBtnClick = true;
                    isSsettingbtnClick = true;
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
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
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isParamtrBtnClick) {
                    TruckParameterFragment truckParameterFragment = new TruckParameterFragment();
                    loadFragment(truckParameterFragment);
                    titleTag.setText("Truck Parameters");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = false;
                    isAertBtnClick = true;
                    isSsettingbtnClick = true;
                    isDrivingBtnClick = true;
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
                    isHelpbtnClick = true;
                }

                break;

            case R.id.driving_data:
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                driving_data_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isDrivingBtnClick) {
                    DrivingDataFragment drivingDataFragment = new DrivingDataFragment();
                    loadFragment(drivingDataFragment);
                    titleTag.setText("Driving Data");

                    isDrivingBtnClick = false;
                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isSsettingbtnClick = true;
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
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
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isSsettingbtnClick) {
                    SettingsFragment settingsFragment = new SettingsFragment();
                    loadFragment(settingsFragment);
                    titleTag.setText("Settings");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isDrivingBtnClick = true;
                    isAertBtnClick = true;
                    isSsettingbtnClick = false;
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
                    isHelpbtnClick = true;
                }

                break;
            case R.id.packages_btn:
                packages_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isPackageBtnClick) {
                    PackagesFragment packagesFragment = new PackagesFragment();
                    loadFragment(packagesFragment);
                    titleTag.setText("Packages");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isAertBtnClick = true;
                    isSsettingbtnClick = true;
                    isPackageBtnClick = false;
                    isSensorBtnClick = true;
                    isDrivingBtnClick = true;
                    isHelpbtnClick = true;
                }
                break;
            case R.id.sensor_btn:
                sensors_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                driving_data_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                maps_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

                if (isSensorBtnClick) {
                    SensorsFragment sensorsFragment = new SensorsFragment();
                    loadFragment(sensorsFragment);
                    titleTag.setText("Sensors");

                    isMapBtnClick = true;
                    isListBtnClick = true;
                    isParamtrBtnClick = true;
                    isDrivingBtnClick = true;
                    isAertBtnClick = true;
                    isSsettingbtnClick = true;
                    isPackageBtnClick = true;
                    isSensorBtnClick = false;
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
                packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
                sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

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
                    isPackageBtnClick = true;
                    isSensorBtnClick = true;
                    isHelpbtnClick = false;
                }

                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        if (!titleTag.getText().toString().equals("Maps")) {

            MapsFragment mapsFrag = new MapsFragment();
            loadFragment(mapsFrag);
            maps_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            vehicle_list_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            config_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            car_parametr_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            setting_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            packages_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            sensors_btn.setBackgroundColor(getResources().getColor(android.R.color.white));
            help_btn.setBackgroundColor(getResources().getColor(android.R.color.white));

            titleTag.setText("Maps");
            } else {
                finishAffinity();
            }
    }

/*    public void call(String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getTrucksFromDB(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("volley Error ................."+ volleyError);
                }
            }) {
                /** Passing some request headers* *//*
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> headers = new HashMap();
                    headers.put("ContentType","applicationvndonem2mresjson");
                    headers.put("X-M2M-Origin","iotsandboxciscocom10000");
                    headers.put("X-M2M-RI","12345");
                    return headers;
                }
            };
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }
*/

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
