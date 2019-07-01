package com.tekfocal.assetmanagementsystem.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.tekfocal.assetmanagementsystem.Adapters.AlertListViewAdapter;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.Models.Alert;
import com.tekfocal.assetmanagementsystem.R;
import com.tekfocal.assetmanagementsystem.SharedPreference.MySharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleAlertsFragment extends Fragment {

    RequestQueue queue;
    Handler handler, handler1, handler2, handler3;
    Runnable runnable, runnable1, runnable2, runnable3;
    MySharedPreference mySharedPreference;

    List<Alert> alerts = new ArrayList<>();

    AlertListViewAdapter alertListViewAdapter;
    ListView listView;

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
        handler1 = new Handler();
        handler2 = new Handler();
        handler3 = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/Voltage" + Constant.URL2;
                call(Url);
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnable);

        runnable1 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/CoolentTemp" + Constant.URL2;
                call(Url);
                handler1.postDelayed(this, 2000);
            }
        };
        handler1.post(runnable1);

        runnable2 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/speed" + Constant.URL2;
                call(Url);
                handler2.postDelayed(this, 2000);
            }
        };
        handler2.post(runnable2);

        runnable3 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/rpm" + Constant.URL2;
                call(Url);
                handler3.postDelayed(this, 1500);
            }
        };
        handler3.post(runnable3);

        return v;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        handler1.removeCallbacks(runnable1);
        handler2.removeCallbacks(runnable2);
        handler3.removeCallbacks(runnable3);

        queue.cancelAll("alerts");
        super.onPause();
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getAlertsFromServer(response, url);
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

    public  void getAlertsFromServer(String response, String url){
        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                    if (jsonArrayResponse.has("con")) {
                        if (url.contains("Voltage")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;

                            if (mySharedPreference.getAlertsData(Constant.V_BATTERY_ALERT)) {
                                try {
                                    if (value < Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.V_BATTERY_LIMIT))) {
                                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                                +":"+ s1[1].substring(4, 6);

                                        alertListViewAdapter.upDateEntries(new Alert("Vehicle 1: Battery Low", date));
                                    }
                                }
                                catch (NullPointerException e){
                                }
                            }
                            Log.e("voltage:", "" + value);
                        }

                        if (url.contains("speed")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;

                            if (mySharedPreference.getAlertsData(Constant.V_SPEED_ALERT)) {
                                try {
                                    if (value > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.V_SPEED_LIMIT))) {
                                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                                +":"+ s1[1].substring(4, 6);

                                        alertListViewAdapter.upDateEntries(new Alert("Vehicle 1: Over Speed", date));
                                    }
                                }
                                catch (NullPointerException e){
                                }
                            }
                        }

                        if (url.contains("rpm")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("RPM:", "" + value);
                        }

                        if (url.contains("CoolentTemp")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;

                            if (mySharedPreference.getAlertsData(Constant.V_TEMP_ALERT)) {
                                try {
                                    if (value > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.V_TEMP_LIMIT))) {
                                        String[] s1 = jsonArrayResponse.getString("ct").toString().split("T");
                                        String date = s1[0].substring(0 ,4) +"-"+ s1[0].substring(4, 6)
                                                +"-"+ s1[0].substring(6, 8)+" "+ s1[1].substring(0, 2) +":"+ s1[1].substring(2, 4)
                                                +":"+ s1[1].substring(4, 6);

                                        alertListViewAdapter.upDateEntries(new Alert("Vehicle 1: Coolant Temperature Exceeded limit", date));
                                    }
                                }
                                catch (NullPointerException e){
                                }
                            }
                            Log.e("CoolantTemp:", "" + value);
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Vehicle Para error:", "" + e);
            }
        }

    }

}
