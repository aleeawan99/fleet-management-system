package com.tekfocal.assetmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    TextView check;
    Button assetManagementBtn, smartAutoMateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        assetManagementBtn = findViewById(R.id.assetManagmnet);
        smartAutoMateBtn = findViewById(R.id.smartAutoMate);

        assetManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()){
                    Toast.makeText(SplashScreen.this,"No Internet Connection!", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }
        });

        smartAutoMateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()){
                    Toast.makeText(SplashScreen.this,"No Internet Connection!", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(SplashScreen.this, Main2Activity.class));
                    finish();
                }
            }
        });

        check = findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOnline()){
            Toast.makeText(this,"No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else {
            check.setText("Welcome...");
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
