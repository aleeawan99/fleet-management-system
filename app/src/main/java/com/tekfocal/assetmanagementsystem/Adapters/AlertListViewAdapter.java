package com.tekfocal.assetmanagementsystem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tekfocal.assetmanagementsystem.Models.Alert;
import com.tekfocal.assetmanagementsystem.Models.Vehicle;
import com.tekfocal.assetmanagementsystem.R;

import java.util.List;

public class AlertListViewAdapter extends BaseAdapter {

    List<Alert> alerts;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public AlertListViewAdapter(Context context, List<Alert> alerts) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.alerts = alerts;
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public Object getItem(int i) {
        return alerts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.alert_list_view, null);
        }

        if (alerts != null) {
            TextView event = view.findViewById(R.id.event);
            TextView time = view.findViewById(R.id.time);

            event.setText(alerts.get(position).getEvent());
            time.setText(alerts.get(position).getTime());
            }
        return view;
    }

    public void upDateEntries(Alert add) {
        this.alerts.add(0, add);
        notifyDataSetChanged();
    }
}