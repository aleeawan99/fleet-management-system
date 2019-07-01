package com.tekfocal.assetmanagementsystem.Fragments;

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
import com.tekfocal.assetmanagementsystem.SharedPreference.MySharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class VehicleParameterFragment extends Fragment {

    private static final String TAG = "Vehicle Parameter";

    private CustomGauge temperature_meter;
    private CustomGauge humidity_meter, speed_meter, engine_rpm_meter, engine_load_meter, coolant_temperature_meter;

    int i;
    private TextView meterName, meterName2, temperature_text_v, humidity_text_v, speed_text_v, engine_rpm_text_v, engine_load_text_v, coolant_text_v;

    RequestQueue queue;
    Handler handler, handler1, handler2, handler3, handler4, handler5;
    Runnable runnable, runnable1, runnable2, runnable3, runnable4, runnable5;
    Thread thread;

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "com.tekfocal.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    Boolean isNotify = true, isBatteryNotify = true, isTempNotify = true;

    MySharedPreference mySharedPreference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.truck_parameter_fragment, container, false);

        queue = Volley.newRequestQueue(getActivity());

        temperature_meter = v.findViewById(R.id.temperature_meter);
        humidity_meter = v.findViewById(R.id.humidity_meter);
        speed_meter = v.findViewById(R.id.speed_meter);
        engine_rpm_meter = v.findViewById(R.id.engine_rpm_meter);
        engine_load_meter = v.findViewById(R.id.engine_load_meter);
        coolant_temperature_meter = v.findViewById(R.id.coolant_meter);

        temperature_text_v = v.findViewById(R.id.temperature_txt_view);
        humidity_text_v = v.findViewById(R.id.humidity_txt_view);
        speed_text_v = v.findViewById(R.id.speed_txt_view);
        engine_rpm_text_v = v.findViewById(R.id.rpm_txt_view);
        engine_load_text_v = v.findViewById(R.id.load_txt_view);
        coolant_text_v = v.findViewById(R.id.coolant_txt_view);

        meterName = v.findViewById(R.id.textView13);
        meterName2 = v.findViewById(R.id.textView1);
        mySharedPreference = new MySharedPreference(getActivity());

        meterName.setText("Voltage");
        meterName2.setText("Throttle Position");

        temperature_text_v.setText("0V");
        humidity_text_v.setText("0%");

        handler = new Handler();
        handler1 = new Handler();
        handler2 = new Handler();
        handler3 = new Handler();
        handler4 = new Handler();
        handler5 = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/Voltage" + Constant.URL2;
                call(Url);
                handler.postDelayed(this, 1500);
            }
        };
        handler.post(runnable);

        runnable1 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/ThrottlePosition" + Constant.URL2;
                call(Url);
                handler1.postDelayed(this, 1500);
            }
        };
        handler1.post(runnable1);

        runnable2 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/speed" + Constant.URL2;
                call(Url);
                handler2.postDelayed(this, 1500);
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

        runnable4 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/EngineLoad" + Constant.URL2;
                call(Url);
                handler4.postDelayed(this, 1500);
            }
        };
        handler4.post(runnable4);

        runnable5 = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.V_URL1 + "vehicle1/CoolentTemp" + Constant.URL2;
                call(Url);
                handler5.postDelayed(this, 1500);
            }
        };
        handler5.post(runnable5);

        return v;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        handler1.removeCallbacks(runnable1);
        handler2.removeCallbacks(runnable2);
        handler3.removeCallbacks(runnable3);
        handler4.removeCallbacks(runnable4);
        handler5.removeCallbacks(runnable5);

        queue.cancelAll("meter");
        super.onPause();
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getTrucksFromDB(response, url);
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
            stringRequest.setTag("meter");
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }

    public void getTrucksFromDB(String response, String url){

        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                for (int i = 0; i < jsonArrayResponse.length(); i++) {
                    if (jsonArrayResponse.has("con")) {
                        if (url.contains("Voltage")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("voltage:", "" + value);
                            setTemperature_meter(value);
                        }
                        if (url.contains("ThrottlePosition")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("ThrottlePosition:", "" + value);
                            setHumidity_meter(value);
                        }
                        if (url.contains("speed")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("Speed:", "" + value);
                            setSpeedmeter(value);
                        }
                        if (url.contains("rpm")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("RPM:", "" + value);
                            setRPMmeter(value);
                        }
                        if (url.contains("EngineLoad")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("EngineLoad:", "" + value);
                            setEngine_load_meter(value);
                        }
                        if (url.contains("CoolentTemp")) {
                            String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                            float f= Float.parseFloat(s[0]);
                            int value = (int) f;
                            Log.e("CoolentTemp:", "" + value);
                            setCoolant_temperature_meter(value);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Vehicle Para error:", "" + e);
            }
        }
    }

    public void setTemperature_meter(int temp){
        if (mySharedPreference.getAlertsData(Constant.V_BATTERY_ALERT)) {
            try {
                if (temp < Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.V_BATTERY_LIMIT))) {
                    if (isBatteryNotify) {
                        sendNotification("Vehicle's Battery Low.");
                        isBatteryNotify = false;
                    }
                } else
                    isBatteryNotify = true;
            }
            catch (NullPointerException e){
                Log.e(TAG, "" + e);
            }
        }

        temperature_meter.setValue(temp);
        temperature_text_v.setText(Integer.toString(temperature_meter.getValue())+ "V");
    }

    public void setHumidity_meter(int humidity){
        humidity_meter.setValue(humidity);
        humidity_text_v.setText(Integer.toString(humidity_meter.getValue())+ "%");
    }

    public void setSpeedmeter(int speed){

        if (mySharedPreference.getAlertsData(Constant.V_SPEED_ALERT)) {
            try {
                if (speed > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.V_SPEED_LIMIT))) {
                    if (isNotify) {
                        sendNotification("Vehicle exceeds speed limit.");
                        isNotify = false;
                    }
                } else
                    isNotify = true;
            }
            catch (NullPointerException e){
                Log.e(TAG, "" + e);
            }
        }
        speed_meter.setValue(speed);
        speed_text_v.setText(Integer.toString(speed_meter.getValue())+ "km/h");
    }

    public void setRPMmeter(int rpm){
        try {
            engine_rpm_meter.setValue(rpm);
            engine_rpm_text_v.setText(Integer.toString(engine_rpm_meter.getValue()) + "rpm");
        }catch (NullPointerException e){
            Log.e(TAG, "" + e);
        }
    }

    public void setEngine_load_meter(int load){
        engine_load_meter.setValue(load);
        engine_load_text_v.setText(Integer.toString(engine_load_meter.getValue())+ "%");
    }

    public void setCoolant_temperature_meter(int temp){
        if (mySharedPreference.getAlertsData(Constant.V_TEMP_ALERT)) {
            try {
                if (temp > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.V_TEMP_LIMIT))) {
                    if (isTempNotify) {
                        sendNotification("Vehicle exceeds temperature limit.");
                        isTempNotify = false;
                    }
                } else
                    isTempNotify = true;
            }
            catch (NullPointerException e){
                Log.e(TAG, "" + e);
            }
        }

        coolant_temperature_meter.setValue(temp);
        coolant_text_v.setText(Integer.toString(coolant_temperature_meter.getValue())+ "\u2103");
    }

    private void sendNotification(String enter) {

        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            mBuilder = new NotificationCompat.Builder(getActivity(), ANDROID_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle("Smart Auto-Mate")
                    .setContentText(enter)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        else{
            mBuilder = new NotificationCompat.Builder(getActivity())
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle("Smart Auto-Mate")
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
