package com.tekfocal.assetmanagementsystem.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tekfocal.assetmanagementsystem.Models.Package;
import com.tekfocal.assetmanagementsystem.R;

import java.util.List;

public class PackagesListViewAdapter extends ArrayAdapter<Package> {

    private int layoutResource;

    public PackagesListViewAdapter(Context context, int layoutResource, List<Package> alerts) {
        super(context, layoutResource, alerts);
        this.layoutResource = layoutResource;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Package aPackage = getItem(position);

        if (aPackage != null) {

            TextView title = view.findViewById(R.id.package_title);
            TextView route = view.findViewById(R.id.route);
            TextView status = view.findViewById(R.id.status);

            if (aPackage.getRoute().contains("IR")){
                route.setBackgroundColor(android.R.color.transparent);
                status.setBackgroundColor(android.R.color.transparent);
            }
            else {
                route.setBackgroundColor(android.R.color.holo_red_light);
                status.setBackgroundColor(android.R.color.holo_red_light);
            }

            title.setText(aPackage.getTitle());
            route.setText(aPackage.getRoute());
            status.setText(aPackage.getStatus());
        }
        return view;
    }

}