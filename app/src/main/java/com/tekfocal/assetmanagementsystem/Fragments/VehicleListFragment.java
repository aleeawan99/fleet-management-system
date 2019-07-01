package com.tekfocal.assetmanagementsystem.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tekfocal.assetmanagementsystem.Adapters.VehicleListAdapter;
import com.tekfocal.assetmanagementsystem.Models.Vehicle;
import com.tekfocal.assetmanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class VehicleListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.vehicle_list_fragment, container, false);


        List<Vehicle> vehicles = new ArrayList<>();

        vehicles.add(new Vehicle("Cell Senal", "2018", true));;

        ListView listView = (ListView)v.findViewById(R.id.vehicle_info_list_view);
        VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(getActivity(), R.layout.vehicle_list_view, vehicles);
        listView.setAdapter(vehicleListAdapter);

        return v;
    }
}
