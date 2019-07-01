package com.tekfocal.assetmanagementsystem.Fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tekfocal.assetmanagementsystem.DrivingDataActivity;
import com.tekfocal.assetmanagementsystem.R;

import static android.icu.lang.UProperty.INT_START;

public class DrivingDataFragment extends Fragment implements View.OnClickListener{

    TextView mileageTextViewBtn, fuelTextBtn, drivingTimeBtn, badDrivingBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.driving_data_fragment, container, false);

        mileageTextViewBtn = v.findViewById(R.id.mileag_btn);
        fuelTextBtn = v.findViewById(R.id.fuel_btn);
        drivingTimeBtn = v.findViewById(R.id.driving_time_btn);
        badDrivingBtn = v.findViewById(R.id.bad_driving_btn);

        mileageTextViewBtn.setOnClickListener(this);
        fuelTextBtn.setOnClickListener(this);
        drivingTimeBtn.setOnClickListener(this);
        badDrivingBtn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.mileag_btn:

                moveToDrivingDataActivity("mileage");
                break;
            case R.id.fuel_btn:

                moveToDrivingDataActivity("fuel");
                break;
            case R.id.driving_data:

                moveToDrivingDataActivity("driving");
                break;
            case R.id.bad_driving_btn:

                moveToDrivingDataActivity("bad driving");
                break;
        }
    }

    public void moveToDrivingDataActivity(String extra){
        Intent intent = new Intent(getActivity(), DrivingDataActivity.class);
        intent.putExtra("Driving Data", extra);
        startActivity(intent);
    }
}
