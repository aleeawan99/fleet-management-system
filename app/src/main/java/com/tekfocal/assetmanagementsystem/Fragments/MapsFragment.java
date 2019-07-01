package com.tekfocal.assetmanagementsystem.Fragments;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.MainActivity;
import com.tekfocal.assetmanagementsystem.Models.Truck;
import com.tekfocal.assetmanagementsystem.R;
import com.tekfocal.assetmanagementsystem.SharedPreference.MySharedPreference;
import com.tekfocal.assetmanagementsystem.Utils.DataParser;
import com.tekfocal.assetmanagementsystem.Volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private Bundle bundle;
    private String seletedTruck;
    Spinner mSpinnerTruckName;
    ArrayList<String> truckList;
    RequestQueue queue;

    ArrayList<Truck> userModel = new ArrayList<>();
    Marker marker, SourceMarker, WayPointMarker, DestinationMarker;
    List<LatLng> path1 = new ArrayList();

    Handler handler;
    Runnable runnable;
    LatLng source, wayPoint, dest, longClickPosition;

    Map<String, Boolean> alertsSwithesValue;
    MySharedPreference mySharedPreference;

    Boolean isMarker = true;
    Boolean isSourceMarker = true, preTimeStamp = true;
    Boolean isDestinationMarker = false, isNotify = true, isNotify2 = true, isNotify3 = true, isEnter = false, isEnter2 = false,
             isEnter3 = false, isRouteNotify = true;

    HashMap<String, LatLng> geoFencesListMap = new HashMap<String, LatLng>();
    HashMap<String, LatLng> routeGeoFence = new HashMap<>();

    Button engineStatus, overSpeeding, Geo_fence;

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "com.tekfocal.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";

    int pre, count = 0;
    String radius, prevTimeStamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.maps_fragment, container, false);

        bundle = this.getArguments();
        seletedTruck = bundle.getString("selectedtruck");
        Log.e("", seletedTruck);

        engineStatus = v.findViewById(R.id.engine_status);
        overSpeeding = v.findViewById(R.id.over_speeding);
        Geo_fence = v.findViewById(R.id.Geo_fence);

        mapView = v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment

        mSpinnerTruckName = v.findViewById(R.id.truck_spinner);

        alertsSwithesValue = new HashMap<String, Boolean>();
        mySharedPreference = new MySharedPreference(getActivity());

        if (!mySharedPreference.getAlertsData(Constant.SPEED_ALERT)) {
            alertsSwithesValue.put("overspeed", true);
            mySharedPreference.setOverSpeedAlert(alertsSwithesValue, "20");
        }

        if (!mySharedPreference.getAlertsData(Constant.TEMP_ALERT)) {
            alertsSwithesValue.put("temp", true);
            mySharedPreference.setHighTempAlert(alertsSwithesValue, "100");
        }

        queue = Volley.newRequestQueue(getActivity());

        call(Constant.GET_TRUCKS);

        return v;
    }

    @Override
    public void onPause() {
        try {
            handler.removeCallbacks(runnable);
            queue.cancelAll("location");
        }catch (NullPointerException e){
            Log.e("Maps Fragment", "" + e);
        }
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                longClickPosition = latLng;
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                final EditText edittext = new EditText(getActivity());
                edittext.setHint("Enter radius");
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setTitle("Enter radius of Geo-fence.");
                alert.setView(edittext);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int whichButton) {

                        radius = edittext.getText().toString();
                        if (TextUtils.isEmpty(radius)){
                            edittext.setError("Enter Radius!");
                            Toast.makeText(getContext(), "Try Again! And enter radius carefully.", Toast.LENGTH_SHORT).show();
                        }else {
                            routeGeoFence.put("route", longClickPosition);
                            mySharedPreference.setRouteGeofence(routeGeoFence, radius);
                            Log.e("Dialog", radius + "" + longClickPosition);
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

        });

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
       // mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM , RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

    }

    public void truckSpinnerFunc(){

        try {

            mSpinnerTruckName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {

                    if (!parent.getSelectedItem().toString().equals("Select")) {

                        final String url=Constant.URL1+parent.getSelectedItem()+"/latlong"+Constant.URL2;
//                    Focustruck=mSpinnerTruckName.getSelectedItem().toString();
//                    userMarkers = new ArrayList<Marker>();
                        //selectedTruck = (String) parent.getSelectedItem();

                        handler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                call(url);
                                call(Constant.URL1 + parent.getSelectedItem() + "/speed" + Constant.V_URL2);
                                call(Constant.URL1 + parent.getSelectedItem() + "/rpm" + Constant.V_URL2);

                                handler.postDelayed(this, 1200);
                            }
                        };
                        handler.post(runnable);
                        call(Constant.URL1 + parent.getSelectedItem() + Constant.GeoFenceUrl);
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (NullPointerException e){
            Log.e("Spinner Error:",""+ e);
        }
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    if (url.contains("fu=1"))
                        getTrucksFromDB(response);
                    else if (url.contains("map_swd")){
                        getSourceAndDestinationWayPoints(response);
                    }

                    else if (url.contains("speed")){
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                            if (jsonArrayResponse.has("con")) {
                                String[] s = jsonArrayResponse.getString("con").toString().split(" ");
                                float f = Float.parseFloat(s[0]);
                                int value = (int) f;
                                if (mySharedPreference.getAlertsData(Constant.SPEED_ALERT)) {
                                    try {
                                        if (value > Integer.valueOf(mySharedPreference.getOverSpeedLimit(Constant.SPEED_LIMIT))) {
                                            overSpeeding.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                                        }
                                        else
                                            overSpeeding.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_green_light));
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

                    else if (url.contains("rpm")) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                            String[] s = jsonArrayResponse.getString("ct").toString().split("T");
                            if (preTimeStamp){
                                prevTimeStamp = s[1];
                                preTimeStamp = false;
                            }
                            else if (prevTimeStamp.equals(s[1])){
                                count++;
                                if (count > 50){
                                    engineStatus.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                                }
                            }
                            else{
                                preTimeStamp = true;
                                count = 0;
                                engineStatus.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_green_light));
                            }
                        }catch (Exception e){}
                    }

                    else
                        parse(response);
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
            stringRequest.setTag("location");
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }

    public void getTrucksFromDB(String response){

        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            truckList = new ArrayList<String>();
            truckList.add("Select");
            int truckCount = 0;
            try {
                JSONArray jsonArrayResponse = new JSONArray(response);

                Truck truck = new Truck();
                for (int i = 0; i < jsonArrayResponse.length(); i++) {
                    JSONObject jsonobj = jsonArrayResponse.getJSONObject(i);

                    if (jsonobj.has("m2m:ae")) {
                        JSONObject obj = new JSONObject(jsonobj.getString("m2m:ae"));
                        String truck1 = obj.getString("aei");
                        if (truck1.toUpperCase().startsWith("T")) {
                            truckCount++;
                            truckList.add(truck1);

                            truck.setName(truck1);
                            truck.setPoistion(new LatLng(0,0));
                            userModel.add(userModel.size(), truck);
                        }
                    }
                }
                Collections.sort(truckList);

                ArrayAdapter<String> adapter_option=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,truckList);
                adapter_option.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                try {
                    mSpinnerTruckName.setAdapter(adapter_option);
                    truckSpinnerFunc();
                } catch (NullPointerException e){
                    Log.e("Spinner Error:",""+ e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Maps Volley error:",""+ e);
            }
        }
    }

    public void parse(String response) {

        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            LatLng position = null;
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                    if (jsonArrayResponse.has("con")) {
                        String latlog = jsonArrayResponse.getString("con");
                        String[] LatLogArray = latlog.split(",");
                        String userName = "Truck 1";

                        double latitude, longitude;
                        try {
                            try {
                                latitude = Double.valueOf(LatLogArray[0]);
                                longitude = Double.valueOf(LatLogArray[1]);
                            } catch (NumberFormatException e) {
                                latitude = 0;
                                longitude = 0;
                            }
                            position = new LatLng(latitude, longitude);
                            Log.e("Maps Lat Long:", "" + position);
                        }catch (ParseException e){
                            Log.e("MapsFrag", "" + e);
                        }
                        try {
                            if (isMarker) {
                                isMarker = false;
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(position)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.truckmarker))
                                        .title("Truck - 1")
                                        .anchor(0.5f, 1));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                            } else {
                                animateMarker(marker, position, false);

                                    Location targetLocation = new Location("");//provider name is unnecessary
                                    targetLocation.setLatitude(geoFencesListMap.get("GeoFence1").latitude);//your coords of course
                                    targetLocation.setLongitude(geoFencesListMap.get("GeoFence1").longitude);

                                    Location markerLocation = new Location("");//provider name is unnecessary
                                    markerLocation.setLatitude(position.latitude);//your coords of course
                                    markerLocation.setLongitude(position.longitude);

                                    float distanceInMeters = markerLocation.distanceTo(targetLocation);

                                    if (distanceInMeters > mySharedPreference.getGeofenceCircleRadius(Constant.SourceGeofenceRadius)) {
                                        isNotify = true;
                                        if (isEnter){
                                            if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                                sendNotification("Truck 1 exit from Source Geofence");
                                            }
                                            Geo_fence.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                                            isEnter = false;
                                        }
                                    } else {
                                        if (isNotify) {
                                            if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                                sendNotification("Truck 1 enters into Source Geofence");
                                            }
                                            Geo_fence.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_green_light));
                                            isEnter = true;
                                            isNotify = false;
                                        }
                                    }

                                targetLocation.setLatitude(geoFencesListMap.get("GeoFence2").latitude);//your coords of course
                                targetLocation.setLongitude(geoFencesListMap.get("GeoFence2").longitude);
                                distanceInMeters = markerLocation.distanceTo(targetLocation);
                                if (distanceInMeters > mySharedPreference.getGeofenceCircleRadius(Constant.DestinationGeofenceRadius)) {
                                    isNotify2 = true;
                                    if (isEnter2){
                                        if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                            sendNotification("Truck 1 exit from Destination Geofence");
                                        }
                                        Geo_fence.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                                        isEnter2 = false;
                                    }
                                } else {
                                    if (isNotify2) {
                                        if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                            sendNotification("Truck 1 enters into Destination Geofence");
                                        }
                                        Geo_fence.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_green_light));
                                        isEnter2 = true;
                                        isNotify2 = false;
                                    }
                                }

                                targetLocation.setLatitude(geoFencesListMap.get("WayPointGeoFence").latitude);//your coords of course
                                targetLocation.setLongitude(geoFencesListMap.get("WayPointGeoFence").longitude);
                                distanceInMeters = markerLocation.distanceTo(targetLocation);
                                if (distanceInMeters > mySharedPreference.getGeofenceCircleRadius(Constant.WayPointGeofenceRadius)) {
                                    isNotify3 = true;
                                    if (isEnter3){
                                        if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                            sendNotification("Truck 1 exit from WayPoint Geofence");
                                        }
                                        Geo_fence.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_red_light));
                                        isEnter3 = false;
                                    }
                                } else {
                                    if (isNotify3) {
                                        if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                            sendNotification("Truck 1 enters into WayPoint Geofence");
                                        }
                                        Geo_fence.setBackgroundColor(getActivity().getResources().getColor(android.R.color.holo_green_light));
                                        isEnter3 = true;
                                        isNotify3 = false;
                                    }
                                }

                                if (mySharedPreference.getAlertsData(Constant.GEO_ALERT)) {
                                    if (mySharedPreference.getRouteGeofence(Constant.ROUTE_GEO_LAT) > 0){
                                        targetLocation.setLatitude(mySharedPreference.getRouteGeofence(Constant.ROUTE_GEO_LAT));
                                        targetLocation.setLongitude(mySharedPreference.getRouteGeofence(Constant.ROUTE_GEO_LOG));
                                        distanceInMeters = markerLocation.distanceTo(targetLocation);
                                        try {
                                            if (distanceInMeters > Integer.parseInt(mySharedPreference.getOverSpeedLimit(Constant.ROUTE_GEO_RADIUS)))
                                            {
                                                if (isRouteNotify) {
                                                    sendNotification("Truck 1 exit from Main Route Geofence");
                                                    isRouteNotify = false;
                                                }
                                            } else {
                                                isRouteNotify = true;
                                            }
                                        }catch (NumberFormatException e){}
                                    }
                                }
                            }
                        }catch (Exception e){ }
                    }
            }catch (JSONException e) {
                e.printStackTrace();
                Log.e("Maps Lat Long error:", "" + e);
            }
        }
    }

    public void getSourceAndDestinationWayPoints(String response) {

        if (response.length() > 0) {
            LatLng position;
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                if (jsonArrayResponse.has("con")) {
                    String latlog = jsonArrayResponse.getString("con");
                    JSONArray LatLogObj = new JSONArray(latlog);

                    JSONObject lat = LatLogObj.getJSONObject(0);
                    String userName = "Truck 1";
                    double latitude = Double.valueOf(lat.getString("lat"));
                    double longitude = Double.valueOf(lat.getString("lng"));
                    position = new LatLng( latitude, longitude);
                  //  path.add(position);
                    source = position;
                    drawGeofenceAndPolyLineDirection(position, mySharedPreference.getGeofenceCircleRadius(Constant.SourceGeofenceRadius));
                    geoFencesListMap.put("GeoFence1",position);
                    Log.e("GeoFence1:", "" + position);

                    lat = LatLogObj.getJSONObject(1);
                    latitude = Double.valueOf(lat.getString("lat"));
                    longitude = Double.valueOf(lat.getString("lng"));
                    position = new LatLng(latitude, longitude);
                   // path.add(position);
                    wayPoint = position;
                    drawGeofenceAndPolyLineDirection(position, mySharedPreference.getGeofenceCircleRadius(Constant.WayPointGeofenceRadius));
                    geoFencesListMap.put("WayPointGeoFence",position);
                    Log.e("WayPointGeoFence:", "" + position);

                    JSONObject lat1 = LatLogObj.getJSONObject(2);
                    Double latitude1 = Double.valueOf(lat1.getString("lat"));
                    Double longitude1 = Double.valueOf(lat1.getString("lng"));
                    LatLng position1 = new LatLng(latitude1, longitude1);
                   // path.add(position1);
                    dest = position1;
                    drawGeofenceAndPolyLineDirection(position1, mySharedPreference.getGeofenceCircleRadius(Constant.DestinationGeofenceRadius));
                    geoFencesListMap.put("GeoFence2",position1);
                    Log.e("GeoFence2:", "" + position1);

                }
            }catch (JSONException e) {
                e.printStackTrace();
                Log.e("Maps Lat Long error:", "" + e);
            }
        }

    }

    //Function to draw circle
    private void drawGeofenceAndPolyLineDirection(LatLng loc, float radius) {

        if (isSourceMarker) {

            CircleOptions circleOptions = new CircleOptions()
                    .center(loc)
                    .strokeColor(Color.argb(40, 70, 10, 10))
                    .fillColor(Color.argb(10, 100, 100, 100))
                    .radius(radius);
            mMap.addCircle(circleOptions);

            isSourceMarker = false;
            SourceMarker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sicon))
                    .title("Source")
                    .anchor(0.5f, 1));
        }
        else if (isDestinationMarker){

            CircleOptions circleOptions = new CircleOptions()
                    .center(loc)
                    .strokeColor(Color.argb(40, 70, 10, 10))
                    .fillColor(Color.argb(10, 100, 100, 100))
                    .radius(radius);
            mMap.addCircle(circleOptions);

            isDestinationMarker = false;
            DestinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dicon))
                    .title("Destination")
                    .anchor(0.5f, 1));

            String url = getWayPointDirectUrl(source, wayPoint, dest);
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(source);
            builder.include(dest);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 40));

        }
        else{
            isDestinationMarker = true;

            WayPointMarker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.wicon))
                    .title("Way Point")
                    .anchor(0.5f, 1));

            // animateMarker(marker ,loc, false);
        }

    }

    private String getWayPointDirectUrl(LatLng source,LatLng wp, LatLng destination) {

        String MY_KEY = "AIzaSyDegxXWoiBKNVjNsJl_fjDACA0hQr56Vyw";
        String str_origin, str_dest, sensor, waypoints ,parameters = "";

        try {
            // Origin of route
             str_origin = "origin=" + source.latitude + "," + source.longitude;

            // Destination of route
             str_dest = "destination=" + destination.latitude + "," + destination.longitude;

            //  enabled
             sensor = "sensor=false";
             waypoints = "waypoints=" + wp.latitude + "," + wp.longitude;

            // Building the parameters to the web service
             parameters = "&" + str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        }catch (NullPointerException e){}

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?key="+ MY_KEY + parameters;

        return url;
    }

    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 160);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
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

    class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    Log.d("ParserTask",jsonData[0].toString());
                    DataParser parser = new DataParser();
                    Log.d("ParserTask", parser.toString());

                    // Starts parsing data
                    routes = parser.parse(jObject);
                    Log.d("ParserTask","Executing routes");
                    Log.d("ParserTask",routes.toString());

                } catch (Exception e) {
                    Log.d("ParserTask",e.toString());
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;
                PolygonOptions polygon = new PolygonOptions();

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                        path1.add(position);

                    }


//                    polygon.addHole(Collections.singleton(points.get(i)));

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);

                    Log.d("onPostExecute","onPostExecute lineoptions decoded");
                }

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null) {
                    polygon.add(new LatLng(-27.457, 153.040),
                            new LatLng(-33.852, 151.211),
                            new LatLng(-37.813, 144.962),
                            new LatLng(-34.928, 138.599))
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.BLACK)
                            .strokeWidth(5);

                    mMap.addPolyline(lineOptions);

                    Polygon polygon1 = mMap.addPolygon(polygon);


                    Log.e("line Positions:", "" + lineOptions.getPoints().toString() + "\n");
                }
                else {
                    Log.d("onPostExecute","without Polylines drawn");
                }
            }
        }
    }


}