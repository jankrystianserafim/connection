package com.example.drone_connection_setup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drone_connection_setup.R;
import com.example.drone_connection_setup.viewmodels.MainActivityViewModel;
import com.example.drone_connection_setup.models.Speed;
import com.example.drone_connection_setup.models.PositionRelative;

import java.util.ArrayList;
import java.util.List;

import io.mavsdk.telemetry.Telemetry;

/*
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(this, "This is a toast", Toast.LENGTH_LONG).show();

    }
*/
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LOG_" + MainActivity.class.getName();
    private static final int REQUEST_CODE_ALL_PERMISSIONS = 1001;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private MainActivityViewModel mViewModel;


    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"some random text",Toast.LENGTH_LONG).show();

        //tv_main_data_distance = findViewById(R.id.tv_main_data_distance);
        //tv_main_data_height = findViewById(R.id.tv_main_data_height);
        //tv_main_data_battery_charge = findViewById(R.id.tv_main_data_battery_charge);
        //tv_main_data_battery_voltage = findViewById(R.id.tv_main_data_battery_voltage);
        //tv_main_data_latitude = findViewById(R.id.tv_main_data_latitude);
        //tv_main_data_longitude = findViewById(R.id.tv_main_data_longitude);
        textView1 = findViewById(R.id.textView1);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        if (checkPermissions()) {
            return;
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CODE_ALL_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ALL_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }
}