package com.example.bluetooth_remote_control_for_arduino.firstPage;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.bluetooth_remote_control_for_arduino.MainActivity2;
import com.example.bluetooth_remote_control_for_arduino.R;
import com.example.bluetooth_remote_control_for_arduino.StreamManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConnectionMainActivity extends AppCompatActivity {

    // Bluetooth Objects
    final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;

    // Items
    Button btn;
    ListView listView;

    // Connected device
    List<BTDevice> btDeviceList = new ArrayList<>();
    String deviceMacAddr;
    String deviceName;


    // threads
    final Boolean ready = false;
    Thread bluetoothSocketConnection;

    //    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind elements
        listView = findViewById(R.id.listView);
        btn = findViewById(R.id.btn1);

        // create a thread for socket connection
        bluetoothSocketConnection = new Thread(new BluetoothCommunication(ConnectionMainActivity.this));
        bluetoothSocketConnection.start();

        // check if device has bluetooth support
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }

        // check for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkForPermissions();
        }

        // enable bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // refresh button
        findDevices();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDevices();
            }
        });

        // list with bluetooth devices
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                deviceName = btDeviceList.get(i).getDeviceName();
                deviceMacAddr = btDeviceList.get(i).getDeviceMACAddr();

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceMacAddr);

                synchronized (ready) {
                    ready.notify();
                }
            }
        });
    }

    void findDevices() {
        btDeviceList.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkForPermissions();
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String devName = device.getName();
                String devHardwareAddress = device.getAddress();

                btDeviceList.add(new BTDevice(devName, devHardwareAddress));
            }

            CustomListAdapter customListAdapter = new CustomListAdapter(ConnectionMainActivity.this, btDeviceList);
            listView.setAdapter(customListAdapter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
    }

    public void openJoyStickActivity() throws InterruptedException {
        bluetoothSocketConnection.join();
        StreamManager.getInstance().setOutputStream(outputStream);
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                Log.d(TAG, "Connection closed");
            } catch (IOException e) {
                Log.d(TAG, "Error while closing the connection");
            }
        }
    }

    @Override
    public void onBackPressed() {
        onDestroy();
        finish();
        System.exit(0);
    }
}