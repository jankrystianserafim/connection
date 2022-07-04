package com.example.drone_connection_setup.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.drone_connection_setup.repositories.DroneRepository;
import com.example.drone_connection_setup.models.PositionRelative;
import com.example.drone_connection_setup.models.Speed;
import io.mavsdk.telemetry.Telemetry;

public class MainActivityViewModel extends AndroidViewModel {

    private DroneRepository mDroneRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mDroneRepository = DroneRepository.getInstance(application);
    }

}


