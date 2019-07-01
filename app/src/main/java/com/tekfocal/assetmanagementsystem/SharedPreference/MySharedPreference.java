package com.tekfocal.assetmanagementsystem.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tekfocal.assetmanagementsystem.Constants.Constant;

import java.util.Map;

public class MySharedPreference  {

    SharedPreferences mPref;
    Context mcontext;
    public MySharedPreference(Context context){
        mcontext = context;
        mPref = mcontext.getSharedPreferences(Constant.MYPREFNAME, Context.MODE_PRIVATE);
  }

    public void saveTruck(String title){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putString(Constant.TRUCK_TITLE,title);
        editor.commit();
    }

    public void saveAlerts(Map<String, Boolean> alerts, String speedLimit){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.SPEED_ALERT, alerts.get("overspeed"));
        editor.putString(Constant.SPEED_LIMIT, speedLimit);
        editor.putBoolean(Constant.GEO_ALERT, alerts.get("geoFenceAlert"));
        editor.putBoolean(Constant.BATTERY_ALERT, alerts.get("carBatteryLow"));
        editor.putBoolean(Constant.TEMP_ALERT, alerts.get("highTemperature"));

        Log.e("saveAlerts:", "" + alerts);

        editor.commit();
    }

    public void setOverSpeedAlert(Map<String, Boolean> alerts, String speedLimit){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.SPEED_ALERT, alerts.get("overspeed"));
        editor.putString(Constant.SPEED_LIMIT, speedLimit);

        Log.e("saveAlerts:", speedLimit + " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setOverSpeedAlertForVehicle(Map<String, Boolean> alerts, String speedLimit){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.V_SPEED_ALERT, alerts.get("overspeedV"));
        editor.putString(Constant.V_SPEED_LIMIT, speedLimit);

        Log.e("saveAlerts:", speedLimit + " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setGeoFenceAlertForVehicle(Map<String, Boolean> alerts){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.V_GEO_ALERT, alerts.get("geoFenceAlertV"));

        Log.e("saveAlerts:",  " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setGeoFenceAlert(Map<String, Boolean> alerts){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.GEO_ALERT, alerts.get("geoFenceAlert"));

        Log.e("saveAlerts:",  " " + alerts);

        editor.commit();
        editor.apply();
    }


    public void setBatteryLowAlertForVehicle(Map<String, Boolean> alerts, String limit){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.V_BATTERY_ALERT, alerts.get("voltageV"));
        editor.putString(Constant.V_BATTERY_LIMIT, limit);

        Log.e("saveAlerts:",  " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setBatteryLowAlert(Map<String, Boolean> alerts){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.BATTERY_ALERT, alerts.get("carBatteryLow"));

        Log.e("saveAlerts:",  " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setHighTempAlertForVehicle(Map<String, Boolean> alerts, String limit){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.V_TEMP_ALERT, alerts.get("tempV"));
        editor.putString(Constant.V_TEMP_LIMIT, limit);

        Log.e("saveAlerts:",  " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setHighTempAlert(Map<String, Boolean> alerts, String limit){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putBoolean(Constant.TEMP_ALERT, alerts.get("temp"));
        editor.putString(Constant.TEMP_LIMIT, limit);

        Log.e("saveAlerts:",  " " + alerts);

        editor.commit();
        editor.apply();
    }

    public void setRouteGeofence(Map<String, LatLng> position, String radius){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putFloat(Constant.ROUTE_GEO_LAT, (float) position.get("route").latitude);
        editor.putFloat(Constant.ROUTE_GEO_LOG, (float) position.get("route").longitude);
        editor.putString(Constant.ROUTE_GEO_RADIUS, radius);

        editor.commit();
        editor.apply();
    }

    public void setRouteGeofenceV(Map<String, LatLng> position, String radius){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putFloat(Constant.V_ROUTE_GEO_LAT, (float) position.get("routeV").latitude);
        editor.putFloat(Constant.V_ROUTE_GEO_LOG, (float) position.get("routeV").longitude);
        editor.putString(Constant.V_ROUTE_GEO_RADIUS, radius);

        editor.commit();
        editor.apply();
    }


    public Boolean getAlertsData(String values){
        Boolean value =  mPref.getBoolean(values, false);
        return  value;
    }

    public String getOverSpeedLimit(String values){
        String value =  mPref.getString(values, "50");
        return  value;
    }

    public void removeData(String key){
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(key).apply();
    }

    public Float getRouteGeofence(String key){
        Float value =  mPref.getFloat(key, (float) 0);
        return  value;
    }

    public void setGeofenceCircleRadius(String key, String radius){
        SharedPreferences.Editor editor  = mPref.edit();
        editor.putString(key, radius);

        editor.commit();
        editor.apply();
    }

    public float getGeofenceCircleRadius(String key){
        float value = Float.parseFloat(mPref.getString(key, "100"));
        return  value;
    }

/*    public boolean isUserLogin(){
        String role =  mPref.getString(Constant.PASSWORD, null);
        String userName =  mPref.getString(Constant.EMAIL, null);
        if(role!=null && userName !=null ) {
            if (!role.equalsIgnoreCase("") && !userName.equalsIgnoreCase("")) {
                return true;
            }
        }
        return false;
    }
*/
}
