package com.tekfocal.assetmanagementsystem.Fragments;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.R;
import com.tekfocal.assetmanagementsystem.SharedPreference.MySharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    Button addSourceGeofenceRadiusBtn, addWayPointGoefenceRadiusBtn, addDestGeofenceRadiusBtn, getGeofenceRadiusFromServerBtn;
    EditText speedLimit, setTempLimit;
    Switch overSpeed, geoFenceAlert, carBatteryLow, highTemperature;
    Map<String, Boolean> alertsSwithesValue;
    MySharedPreference mySharedPreference;
    String speed;
    int speedSharePref;
    String TAG = "Settings Fragment";
    RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container,false);

        addSourceGeofenceRadiusBtn = v.findViewById(R.id.source_geofence_btn);
        addWayPointGoefenceRadiusBtn = v.findViewById(R.id.waypoint_geofence_btn);
        addDestGeofenceRadiusBtn = v.findViewById(R.id.dest_geofence_btn);
        getGeofenceRadiusFromServerBtn = v.findViewById(R.id.server_geofence_btn);

        TextView textView = v.findViewById(R.id.textView11);
        EditText volatge = v.findViewById(R.id.set_volt_alert);
        View line = v.findViewById(R.id.view2);

        setTempLimit = v.findViewById(R.id.set_temp_alert);

        speedLimit = v.findViewById(R.id.editText);
        overSpeed = v.findViewById(R.id.over_speed_alert);
        geoFenceAlert = v.findViewById(R.id.geofence_alert);
        carBatteryLow = v.findViewById(R.id.car_battery_alert);
        highTemperature = v.findViewById(R.id.high_temperature_alert);

        carBatteryLow.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        volatge.setVisibility(View.INVISIBLE);
        line.setVisibility(View.INVISIBLE);

        alertsSwithesValue = new HashMap<String, Boolean>();
        mySharedPreference = new MySharedPreference(getActivity());

        queue = Volley.newRequestQueue(getActivity());

        if (mySharedPreference.getAlertsData(Constant.SPEED_ALERT)) {
            try {
                speedLimit.setText(mySharedPreference.getOverSpeedLimit(Constant.SPEED_LIMIT));
                overSpeed.setChecked(true);
                speedLimit.setEnabled(false);
            }
            catch (NullPointerException e){
                Log.e(TAG, "" + e);
            }
        }

        if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
            geoFenceAlert.setChecked(true);
        }

        if (mySharedPreference.getAlertsData(Constant.TEMP_ALERT)) {
            try {
                setTempLimit.setText(mySharedPreference.getOverSpeedLimit(Constant.TEMP_LIMIT));
                highTemperature.setChecked(true);
                setTempLimit.setEnabled(false);
            }
            catch (NullPointerException e){
                Log.e(TAG, "" + e);
            }
        }

        onButtonClicks();
        getAlertsSwitchesValue();

        return v;
    }

    private void getAlertsSwitchesValue(){

        overSpeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b)
                    speedLimit.setEnabled(true);
                else {
                    if (TextUtils.isEmpty(speedLimit.getText().toString())){
                        Toast.makeText(getActivity(), "Please Enter Speed Limit!", Toast.LENGTH_SHORT).show();
                        overSpeed.setChecked(false);
                    }
                    else {
                        speed = speedLimit.getText().toString();
                        alertsSwithesValue.put("overspeed",b);
                        mySharedPreference.setOverSpeedAlert(alertsSwithesValue, speed);
                        speedLimit.setEnabled(false);
                    }
                }
                Log.e("overspeed::", "" +b);
            }
        });

        geoFenceAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!b){
                    mySharedPreference.removeData(Constant.ROUTE_GEO_LAT);
                    mySharedPreference.removeData(Constant.ROUTE_GEO_LOG);
                    mySharedPreference.removeData(Constant.ROUTE_GEO_RADIUS);
                }
                alertsSwithesValue.put("geoFenceAlert", b);
                mySharedPreference.setGeoFenceAlert(alertsSwithesValue);
                Log.e("geoFenceAlert::", "" + b);
            }
        });

        highTemperature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!b){
                    setTempLimit.setEnabled(true);
                    if (mySharedPreference.getAlertsData(Constant.TEMP_ALERT)) {
                        mySharedPreference.removeData(Constant.TEMP_ALERT);
                    }
                }
                else {
                    if (TextUtils.isEmpty(setTempLimit.getText().toString())){
                        Toast.makeText(getActivity(), "Please Enter Temperature Limit!", Toast.LENGTH_SHORT).show();
                        highTemperature.setChecked(false);
                    }
                    else {
                        speed = setTempLimit.getText().toString();
                        alertsSwithesValue.put("temp",b);
                        mySharedPreference.setHighTempAlert(alertsSwithesValue, speed);
                        setTempLimit.setEnabled(false);
                    }
                }
                Log.e("highTemperature::", "" + b);
            }
        });

    }

    private void onButtonClicks(){

        addSourceGeofenceRadiusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBoxForRadius("source", Constant.SourceGeofenceRadius);
            }
        });


        addWayPointGoefenceRadiusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxForRadius("way-point", Constant.WayPointGeofenceRadius);
            }
        });

        addDestGeofenceRadiusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxForRadius("destination", Constant.DestinationGeofenceRadius);
            }
        });

        getGeofenceRadiusFromServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(Constant.URL1 + "Truck-1" + Constant.GeoFenceUrl );
            }
        });
    }

    public void dialogBoxForRadius(String title, final String key){

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        final EditText edittext = new EditText(getActivity());
        edittext.setHint("Enter radius");
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setTitle("Define radius for " + title + " circle Geo-fence.");
        alert.setView(edittext);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog1, int whichButton) {

                String radius = edittext.getText().toString();
                if (TextUtils.isEmpty(radius)){
                    edittext.setError("Enter Radius!");
                    Toast.makeText(getContext(), "Try Again! And enter radius carefully.", Toast.LENGTH_SHORT).show();
                }else {
                    mySharedPreference.setGeofenceCircleRadius(key, radius);
                    Log.e("radius", radius);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getSourceAndDestinationRadius(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("volley Error ................."+ volleyError);
                }
            }) {
                /** Passing some request headers* */
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> headers = new HashMap();
                    headers.put("ContentType","applicationvndonem2mresjson");
                    headers.put("X-M2M-Origin","iotsandboxciscocom10000");
                    headers.put("X-M2M-RI","12345");
                    return headers;
                }
            };
            stringRequest.setTag("settings");
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }

    public void getSourceAndDestinationRadius(String response) {

        if (response.length() > 0) {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                if (jsonArrayResponse.has("con")) {
                    String radius = jsonArrayResponse.getString("con");
                    JSONArray rObj = new JSONArray(radius);
                    JSONObject r = rObj.getJSONObject(3);
                    float rad = Float.valueOf(r.getString("radius"));

                    mySharedPreference.setGeofenceCircleRadius(Constant.SourceGeofenceRadius, ""+rad);
                    mySharedPreference.setGeofenceCircleRadius(Constant.DestinationGeofenceRadius, ""+rad);

                    Log.e("V Setting Radius:", "" + rad);
                }
            }catch (JSONException e) {
                e.printStackTrace();
                Log.e("setting radius error:", "" + e);
            }
        }

    }

}
