package com.tekfocal.assetmanagementsystem.Fragments;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.tekfocal.assetmanagementsystem.Adapters.AlertListViewAdapter;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.Models.Alert;
import com.tekfocal.assetmanagementsystem.R;
import com.tekfocal.assetmanagementsystem.SharedPreference.MySharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsFragment extends Fragment {

    RequestQueue queue;
    Handler handler, handler1, handler2, handler3, handler4;
    Runnable runnable, runnable1, runnable2, runnable3, runnable4;
    MySharedPreference mySharedPreference;

    List<Alert> alerts = new ArrayList<>();

    AlertListViewAdapter alertListViewAdapter;
    ListView listView;

    HashMap<String, LatLng> geoFencesListMap = new HashMap<String, LatLng>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.alerts_fragment, container, false);

        listView = v.findViewById(R.id.alert_list_view);
        alertListViewAdapter = new AlertListViewAdapter(getActivity(), alerts);
        listView.setAdapter(alertListViewAdapter);

        queue = Volley.newRequestQueue(getActivity());
        mySharedPreference = new MySharedPreference(getActivity());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/gas" + Constant.URL2;
                call(Url);

                call(Constant.URL1 + "Truck-1/speed" + Constant.URL2);
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnable);

        handler1 = new Handler();
        runnable1 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/vibration" + Constant.URL2;
                call(Url);

                call(Constant.URL1 + "Truck-1/CoolentTemp" + Constant.URL2);
                handler1.postDelayed(this, 2000);
            }
        };
        handler1.post(runnable1);

        handler2 = new Handler();
        runnable2 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/light" + Constant.URL2;
                call(Url);
                handler2.postDelayed(this, 2000);
            }
        };
        handler2.post(runnable2);

        handler3 = new Handler();
        runnable3 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/motion" + Constant.URL2;
                call(Url);
                handler3.postDelayed(this, 2000);
            }
        };
        handler3.post(runnable3);

        handler4 = new Handler();
        runnable4 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/tilt" + Constant.URL2;
                call(Url);
                handler4.postDelayed(this, 2000);
            }
        };
        handler4.post(runnable4);

        return v;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        handler1.removeCallbacks(runnable1);
        handler2.removeCallbacks(runnable2);
        handler3.removeCallbacks(runnable3);
        handler4.removeCallbacks(runnable4);

        queue.cancelAll("alerts");
        super.onPause();
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getTruckAlertsFromServer(response, url);
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
            stringRequest.setTag("alerts");
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }

    public void getTruckAlertsFromServer(String response, String url){

        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                    if (url.contains("map_swd")) {
                        if (jsonArrayResponse.has("con")) {
                            String latlog = jsonArrayResponse.getString("con");
                            JSONArray LatLogObj = new JSONArray(latlog);

                            JSONObject lat = LatLogObj.getJSONObject(0);
                            String userName = "Truck 1";
                            double latitude = Double.valueOf(lat.getString("lat"));
                            double longitude = Double.valueOf(lat.getString("lng"));
                            LatLng position = new LatLng(latitude, longitude);
                            geoFencesListMap.put("GeoFence1", position);
                            Log.e("Maps Lat Long:", "" + position);

                            JSONObject lat1 = LatLogObj.getJSONObject(2);
                            Double latitude1 = Double.valueOf(lat1.getString("lat"));
                            Double longitude1 = Double.valueOf(lat1.getString("lng"));
                            LatLng position1 = new LatLng(latitude1, longitude1);
                            geoFencesListMap.put("GeoFence2", position1);
                            Log.e("Maps Lat Long:", "" + position1);
                        }
                    }

                if (url.contains("speed")){
                    try {
                        if (jsonArrayResponse.has("con")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f = Float.parseFloat(s[0]);
                            int value = (int) f;
                            if (mySharedPreference.getAlertsData(Constant.SPEED_ALERT)) {
                                try {
                                    if (value > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.SPEED_LIMIT))) {
                                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                                +":"+ s1[1].substring(4, 6);

                                        alertListViewAdapter.upDateEntries(new Alert("Truck 1: Over Speeding", date));
                                    }
                                }
                                catch (NullPointerException e){
                                }
                            }
                            Log.e("Speed:", "" + value);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (url.contains("CoolentTemp")) {
                    String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                    float f= Float.parseFloat(s[0]);
                    int value = (int) f;

                    if (mySharedPreference.getAlertsData(Constant.TEMP_ALERT)) {
                        try {
                            if (value > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.TEMP_LIMIT))) {
                                String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                                String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                        +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                        +":"+ s1[1].substring(4, 6);

                                alertListViewAdapter.upDateEntries(new Alert("Truck 1: Coolant Temperature Exceeded limit", date));
                            }
                        }
                        catch (NullPointerException e){
                        }
                    }
                    Log.e("CoolantTemp:", "" + value);
                }

                if (jsonArrayResponse.has("con") && jsonArrayResponse.getString("con").contains("True")) {
                    if (url.contains("gas")) {

                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                        +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                            +":"+ s1[1].substring(4, 6);

                        alertListViewAdapter.upDateEntries(new Alert("Truck 1: Gas Detected", date));
                    }

                    if (url.contains("vibration")) {
                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                +":"+ s1[1].substring(4, 6);

                        alertListViewAdapter.upDateEntries(new Alert("Truck 1: Vibration Detected", date));
                    }

                    if (url.contains("light")) {
                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                +":"+ s1[1].substring(4, 6);

                        alertListViewAdapter.upDateEntries(new Alert("Truck 1: Light Detected", date));
                    }

                    if (url.contains("motion")) {
                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                +":"+ s1[1].substring(4, 6);

                        alertListViewAdapter.upDateEntries(new Alert("Truck 1: Motion Detected", date));
                    }

                    if (url.contains("tilt")) {
                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                +":"+ s1[1].substring(4, 6);

                        alertListViewAdapter.upDateEntries(new Alert("Truck 1: Tilt Detected", date));
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
                Log.e("Truck Sensors error:", "" + e);
            }
        }

    }

}
