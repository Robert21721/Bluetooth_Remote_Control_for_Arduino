package com.example.bluetooth_remote_control_for_arduino.firstPage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class BluetoothCommunication implements Runnable {

    ConnectionMainActivity mainActivity;
    UUID uuid;

    public BluetoothCommunication(ConnectionMainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void run() {
        while (true) {
            synchronized (this.mainActivity.ready) {
                try {
                    mainActivity.ready.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }

            try {
                mainActivity.bluetoothSocket = mainActivity.bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                mainActivity.bluetoothAdapter.cancelDiscovery();
                mainActivity.bluetoothSocket.connect();
                mainActivity.outputStream = mainActivity.bluetoothSocket.getOutputStream();

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, "Connected to " + mainActivity.deviceName, Toast.LENGTH_SHORT).show();
                        try {
                            mainActivity.openJoyStickActivity();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                break;

            } catch (IOException e) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity, "Incompatible Device", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}