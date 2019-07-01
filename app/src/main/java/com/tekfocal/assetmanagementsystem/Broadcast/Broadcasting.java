package com.tekfocal.assetmanagementsystem.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by dania on 7/18/2017.
 */

public class Broadcasting extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

      Bundle extras= intent.getExtras();
        if(extras!=null){
            if(extras.containsKey("values")){
                String masg=extras.getString("values");
                Toast.makeText(context, masg, Toast.LENGTH_SHORT).show();

            }
        }
    }




}



