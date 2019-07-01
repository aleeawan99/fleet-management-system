package com.tekfocal.assetmanagementsystem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tekfocal.assetmanagementsystem.Models.Vehicle;
import com.tekfocal.assetmanagementsystem.R;

import java.util.List;

public class VehicleListAdapter extends ArrayAdapter<Vehicle> {

    private int layoutResource;

    public VehicleListAdapter(Context context, int layoutResource, List<Vehicle> vehicles) {
        super(context, layoutResource, vehicles);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Vehicle vehicle = getItem(position);

        if (vehicle != null) {
            final TextView ownerName = view.findViewById(R.id.owner_name);
            TextView model = view.findViewById(R.id.model);
            Switch status= view.findViewById(R.id.status);

            ownerName.setText(vehicle.getOwnerName());
            model.setText(vehicle.getModel());
            if (vehicle.isStatus()){
                status.setChecked(true);
            }
            else {
                status.setChecked(false);
            }

            status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(getContext(),""+ ownerName.getText(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}
