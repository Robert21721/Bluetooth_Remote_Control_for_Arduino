package com.example.bluetooth_remote_control_for_arduino.firstPage;

public class BTDevice {
    String deviceName;
    String deviceMACAddr;

    public BTDevice(String deviceName, String deviceMACAddr) {
        this.deviceName = deviceName;
        this.deviceMACAddr = deviceMACAddr;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public String getDeviceMACAddr() {
        return deviceMACAddr;
    }

}
