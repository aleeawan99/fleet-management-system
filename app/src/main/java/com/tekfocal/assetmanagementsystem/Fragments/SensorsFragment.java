package com.tekfocal.assetmanagementsystem.Fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SensorsFragment extends Fragment {

    TextView gas_text_v, vibration_text_v, light_text_v, motion_text_v, tilt_text_v;
    ImageView gas_symbol, vibration_symbol, light_symbol, motion_symbol, tilt_symbol;
    RequestQueue queue;
    private NotificationManager mManager;

    public static final String ANDROID_CHANNEL_ID = "com.tekfocal.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";

    Handler handler, handler1, handler2, handler3, handler4;
    Runnable runnable, runnable1, runnable2, runnable3, runnable4;
    Boolean isGasNotify = true, isVibNotify = true, isLightNotify = true, isMotionNotify = true, isTiltNotify = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sensors_fragment, container, false);

        queue = Volley.newRequestQueue(getActivity());

        gas_text_v = v.findViewById(R.id.gas_status);
        vibration_text_v = v.findViewById(R.id.vibration_status);
        light_text_v = v.findViewById(R.id.light_status);
        motion_text_v = v.findViewById(R.id.motion_status);
        tilt_text_v = v.findViewById(R.id.tilt_status);

        gas_symbol = v.findViewById(R.id.gas_symbol);
        vibration_symbol = v.findViewById(R.id.vibration_symbol);
        light_symbol = v.findViewById(R.id.light_symbol);
        motion_symbol = v.findViewById(R.id.motion_symbol);
        tilt_symbol = v.findViewById(R.id.tilt_symbol);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/gas" + Constant.URL2;
                call(Url);
                handler.postDelayed(this, 1500);
            }
        };
        handler.post(runnable);

        handler1 = new Handler();
        runnable1 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/vibration" + Constant.URL2;
                call(Url);
                handler1.postDelayed(this, 1500);
            }
        };
        handler1.post(runnable1);

        handler2 = new Handler();
        runnable2 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/light" + Constant.URL2;
                call(Url);
                handler2.postDelayed(this, 1500);
            }
        };
        handler2.post(runnable2);

        handler3 = new Handler();
        runnable3 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/motion" + Constant.URL2;
                call(Url);
                handler3.postDelayed(this, 1500);
            }
        };
        handler3.post(runnable3);

        handler4 = new Handler();
        runnable4 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/tilt" + Constant.URL2;
                call(Url);
                handler4.postDelayed(this, 1500);
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

        queue.cancelAll("sensors");
        super.onPause();
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getTruckSensersFromDB(response, url);
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
            stringRequest.setTag("sensors");
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }

    public void getTruckSensersFromDB(String response, String url){

        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                    if (jsonArrayResponse.has("con") && jsonArrayResponse.getString("con").contains("True")) {
                        if (url.contains("gas")) {
                            gas_text_v.setText("Gas Detected");
                            gas_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                            gas_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                gas_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.gas));
                            } else
                                gas_symbol.setImageResource(R.drawable.gas);
                            if (isGasNotify)
                                sendNotification("Gas Detected!");
                            isGasNotify = false;
                        }
                        else if (url.contains("vibration")) {
                            vibration_text_v.setText("Vibration Detected");
                            vibration_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                            vibration_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                            if (isVibNotify)
                                sendNotification("Vibration Detected!");
                            isVibNotify = false;
                        }
                        else if (url.contains("light")) {
                            light_text_v.setText("Light Detected");
                            light_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                            light_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                light_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.light));
                            } else
                                light_symbol.setImageResource(R.drawable.light);
                            if (isLightNotify)
                                sendNotification("Light Detected!");
                            isLightNotify = false;
                        }
                        else if (url.contains("motion")) {
                            motion_text_v.setText("Motion Detected");
                            motion_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                            motion_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                motion_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.motion));
                            } else
                                motion_symbol.setImageResource(R.drawable.motion);
                            if (isMotionNotify)
                                sendNotification("Motion Detected!");
                            isMotionNotify = false;
                        }
                        else if (url.contains("tilt")) {
                            tilt_text_v.setText("Tilt Detected");
                            tilt_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                            tilt_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tilt_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.tilt));
                            } else
                                tilt_symbol.setImageResource(R.drawable.tilt);
                            if (isTiltNotify)
                                sendNotification("Tilt Detected!");
                            isTiltNotify = false;
                        }

                    } else {
                        if (url.contains("gas")) {
                            gas_text_v.setText("No Gas");
                            gas_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                            gas_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.tertiary_text_dark));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                gas_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.gas_prev));
                            } else
                                gas_symbol.setImageResource(R.drawable.gas_prev);
                            isGasNotify = true;
                        }

                        else if (url.contains("vibration")) {
                            vibration_text_v.setText("No Vibration");
                            vibration_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                            vibration_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.tertiary_text_dark));
                            isVibNotify = true;
                        }

                        else if (url.contains("light")) {
                            light_text_v.setText("No Light");
                            light_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                            light_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.tertiary_text_dark));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                light_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.light_prev));
                            } else
                                light_symbol.setImageResource(R.drawable.light_prev);
                            isLightNotify = true;
                        }

                        else if (url.contains("motion")) {
                            motion_text_v.setText("No Motion");
                            motion_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                            motion_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.tertiary_text_dark));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                motion_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.motion_prev));
                            } else
                                motion_symbol.setImageResource(R.drawable.motion_prev);
                            isMotionNotify = true;
                        }

                        else if (url.contains("tilt")) {
                            tilt_text_v.setText("No Tilt");
                            tilt_text_v.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                            tilt_text_v.setTextColor(getActivity().getResources().getColor(android.R.color.tertiary_text_dark));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tilt_symbol.setImageDrawable(getActivity().getDrawable(R.drawable.tilt_prev));
                            } else
                                tilt_symbol.setImageResource(R.drawable.tilt_prev);
                            isTiltNotify = true;
                        }
                    }
            }catch (JSONException e) {
                e.printStackTrace();
                Log.e("Truck Sensors error:", "" + e);
            }
        }

    }

    private void sendNotification(String enter) {

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            mBuilder = new NotificationCompat.Builder(getActivity(), ANDROID_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle("Assets Management System")
                    .setContentText(enter)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        else{
            mBuilder = new NotificationCompat.Builder(getActivity())
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle("Assets Management System")
                    .setContentText(enter)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());

        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(){
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}
