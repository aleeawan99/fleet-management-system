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
import com.tekfocal.assetmanagementsystem.Adapters.PackagesListViewAdapter;
import com.tekfocal.assetmanagementsystem.Constants.Constant;
import com.tekfocal.assetmanagementsystem.Models.Package;
import com.tekfocal.assetmanagementsystem.R;
import com.tekfocal.assetmanagementsystem.SharedPreference.MySharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackagesFragment extends Fragment {

    RequestQueue queue;
    Handler handler, handler1, handler2, handler3, handler4;
    Runnable runnable, runnable1, runnable2, runnable3, runnable4;

    ListView listView;
    List<Package> packages;
    PackagesListViewAdapter packagesListViewAdapter;

    ArrayList<String> cargoItemsList = new ArrayList<String>();
    ArrayList<Boolean> cargoItemsActive = new ArrayList<Boolean>();

    HashMap<String, Boolean> cargoItems = new HashMap<>();

    String rn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.packages_fragment,container, false);

        packages = new ArrayList<>();

        listView = v.findViewById(R.id.packages_list_view);

        queue = Volley.newRequestQueue(getActivity());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                String Url = Constant.URL1 + "Truck-1/cargoitems" + Constant.URL2;
                call(Url);
                if (cargoItemsList.size() > 0) {
                    for (int i = 0; i < cargoItemsList.size(); i++) {
                        if (cargoItems.get(cargoItemsList.get(i))) {
                            packages.add(new Package(cargoItemsList.get(i), "IR", "Active"));
                        }
                        else {
                            packages.add(new Package(cargoItemsList.get(i), "---", "Inactive"));
                        }
                    }
                    packagesListViewAdapter = new PackagesListViewAdapter(getActivity(), R.layout.package_list_view, packages);
                    listView.setAdapter(packagesListViewAdapter);
                    packages = new ArrayList<>();
                }

                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnable);

        return v;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        queue.cancelAll("packages");
        super.onPause();
    }

    public void call(final String url){

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response::", response);
                    getTruckCargoItemsFromServer(response, url);
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
            stringRequest.setTag("packages");
            queue.add(stringRequest);
        }
        catch (Exception e){
            Log.e("errrr","" + e) ;
        }
    }

    public void getTruckCargoItemsFromServer(String response, String url){

        Log.e("length of reponse::", String.valueOf(response.length()));
        if (response.length() > 0) {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonArrayResponse = object.getJSONObject("m2m:cin");

                long check_rn = 0;
                if (jsonArrayResponse.has("rn")) {

                    if (url.contains("/la")) {

                        rn = jsonArrayResponse.getString("rn");
                        long rn1;
                        try {
                            rn1 = Long.parseLong(rn);
                        }catch (NumberFormatException e){
                            rn1 = 0;
                        }
                        check_rn = rn1;

                        for (int i = 1; i < 9; i++) {
                            long rn2 = rn1 - i;
                            String Url = Constant.URL1 + "Truck-1/cargoitems/" + rn2;

                            call(Url);
                        }
                    }

                    if (url.contains(""+(check_rn - 1))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                                cargoItems.put(jsonArrayResponse.getString("con"), false);
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (url.contains(""+(check_rn - 2))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (url.contains(""+(check_rn - 3))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (url.contains(""+(check_rn - 4))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (url.contains(""+(check_rn - 5))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (url.contains(""+(check_rn - 6))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (url.contains(""+(check_rn - 7))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }

                    if (url.contains(""+(check_rn - 8))) {
                        try {
                            if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                                cargoItems.put(jsonArrayResponse.getString("con"), true);
                                Log.e("List", cargoItemsList.toString());
                                Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                            } else {
                                cargoItemsList.add(jsonArrayResponse.getString("con"));
                            }
                        } catch (Exception e) {
                        }
                    }

                }
            }catch (JSONException e) {
                e.printStackTrace();
                Log.e("Truck Sensors error:", "" + e);
            }
        }
    }

    public void setCargoItems(String url, long check_rn, JSONObject jsonArrayResponse){
        if (url.contains(""+check_rn)) {
            Log.e("call", "" + check_rn + url);
            try {
                if (cargoItemsList.contains(jsonArrayResponse.getString("con"))) {
                    cargoItems.put(jsonArrayResponse.getString("con"), true);
                    Log.e("List", cargoItemsList.toString());
                    Log.e("Active", "" + cargoItems.get(jsonArrayResponse.getString("con")));
                } else {
                    cargoItemsList.add(jsonArrayResponse.getString("con"));
                }
            } catch (Exception e) {
            }
        }
    }

}
