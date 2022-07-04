package com.example.drone_connection_setup.repositories;

import android.app.Application;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
//import androidx.lifecycle.LiveDataReactiveStreams; this needs to be cross checked if relevant
import androidx.lifecycle.LiveData;

import com.example.drone_connection_setup.R;
import com.example.drone_connection_setup.models.PositionRelative;
import com.example.drone_connection_setup.models.Speed;
import com.google.common.collect.Lists;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.mavsdk.System;
import io.mavsdk.mavsdkserver.MavsdkServer;
import io.mavsdk.telemetry.Telemetry;

import io.reactivex.Flowable;

import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.flowables.GroupedFlowable;

public class DroneRepository {
    private static final String TAG = "LOG_" + DroneRepository.class.getName();
    private static final boolean GENERATE_DUMMY_DATA = false;
    private static final boolean IS_SIMULATION = false;

    private static final String NO_ADDRESS = "no_address";
    private static final int USB_BAUD_RATE = 57600;
    private static final String MAVSDK_SERVER_IP = "127.0.0.1";
    private static final long THROTTLE_TIME_MILLIS = 500;

    private static DroneRepository instance;

    private System mDrone;
    private MavsdkServer mMavsdkServer;
    private Context mAppContext;
    private UsbDeviceConnection connection;

    private LiveData<PositionRelative> mPositionRelativeLiveData;
    private LiveData<Speed> mSpeedLiveData;
    private LiveData<Telemetry.Battery> mBatteryLiveData;
    private LiveData<Telemetry.GpsInfo> mGpsInfoLiveData;
    private LiveData<Telemetry.Position> mPositionLiveData;

    public static DroneRepository getInstance(Application application) {
        if (instance == null) {
            instance = new DroneRepository(application);
        }
        return instance;
    }


    private DroneRepository(Application application) {
        mAppContext = application.getApplicationContext();
        initializeServerAndDrone(initializeUsbDevice());
    }

    // Using usb-serial-for-android
    private String initializeUsbDevice() {

        UsbManager usbManager = (UsbManager) mAppContext.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> driverList = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

        if (driverList.isEmpty()) {
            Toast.makeText(mAppContext, R.string.str_usb_device_not_found, Toast.LENGTH_LONG).show();
            return NO_ADDRESS;
        }

        UsbSerialDriver usbSerialDriver = driverList.get(0);
        UsbDevice usbDevice = usbSerialDriver.getDevice();
        boolean hasPermission = usbManager.hasPermission(usbDevice);

        if (!hasPermission) {
            Toast.makeText(mAppContext, R.string.str_usb_permission_not_granted, Toast.LENGTH_LONG).show();
            return NO_ADDRESS;
        }

        if (hasPermission) {
            Toast.makeText(mAppContext, R.string.str_usb_permission_is_granted, Toast.LENGTH_LONG).show();
        }

        connection = usbManager.openDevice(usbDevice);
        UsbSerialPort usbSerialPort = usbSerialDriver.getPorts().get(0);

        try {
            usbSerialPort.open(connection);
            usbSerialPort.setParameters(
                    USB_BAUD_RATE,
                    UsbSerialPort.DATABITS_8,
                    UsbSerialPort.STOPBITS_1,
                    UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "initializeUsbDevice: port isOpen: " + usbSerialPort.isOpen());


        String systemAddress = "serial_fd://" + connection.getFileDescriptor() + ":" + USB_BAUD_RATE;

        Log.d(TAG, "initializeUsbDevice: " + systemAddress);
        return systemAddress;
    }



    private void initializeServerAndDrone(String systemAddress) {
        mMavsdkServer = new MavsdkServer();
        int mavsdkServerPort = mMavsdkServer.run(systemAddress);
        mDrone = new System(MAVSDK_SERVER_IP, mavsdkServerPort);

//        connection.close();
    }



    public void destroy() {
        mDrone.dispose();
        mMavsdkServer.stop();
    }
}
